package ru.practicum.ewm.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.aggregator.producer.EventsSimilarityProducer;
import ru.practicum.ewm.stats.aggregator.util.TableRenderer;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarityServiceImpl implements SimilarityService {

	private final EventsSimilarityProducer producer;

	/**
	 * Матрица ведущих весов<br/>
	 * {@code Map<eventId, Map<userId, maxActionWeight>>}<br/>
	 * где {@code maxActionWeight} - максимальный вес
	 * из всех действий этого пользователя по этому мероприятию
	 */
	Map<Long, Map<Long, Double>> leadingWeightMatrix = new HashMap<>();

	/**
	 * Матрица числителей<br/>
	 * {@code Map<eventId, Map<eventId, SumOfMinWeightsFromMaxActionWeights>>}<br/>
	 * где {@code SumOfMinWeightsFromMaxActionWeights} - сумма минимальных весов
	 * по всем ведущим весам всех действий пользоваелей, для этой пары мероприятий
	 */
	Map<Long, Map<Long, Double>> sumOfMinWeightsMatrix = new HashMap<>();

	/**
	 * Таблица знаминателей<br/>
	 * {@code Map<eventId, leadingWeightsSum>}<br/>
	 * где {@code leadingWeightsSum} - сумма ведущих весов всех
	 * действий пользователей по этому мероприятию
	 */
	Map<Long, Double> eventToLeadingWeightsSum = new HashMap<>();

	/// Посчитать сходство и отправить в кафку
	@Override
	public void collectSimilarity(@NonNull UserActionAvro avro) {

		var userId = avro.getUserId();
		var eventId = avro.getEventId();
		var actionType = avro.getActionType();

		// Получаем ведущий вес
		double newLeadingWeight = calculateLeadingWeight(actionType);

		// Проходим верификацию
		if (!verifyData(userId, eventId, newLeadingWeight)) {
			printMatrix();
			return;
		}

		// Считаем сходство
		var similarities = calculateSimilarities(eventId, userId);
		if (similarities.isEmpty()) {
			log.info("Сходства не обнаружены");
			printMatrix();
			return;
		}

		// Отправляем по одному
		for (EventsSimilarityAvro similarity : similarities) {
			sendSimilarity(eventId, similarity);
		}

		printMatrix();
	}

	// ----------------------------------BUSSINES-LOGIC-------------------------------------

	private boolean verifyData(long userId, long eventId, double newLeadingWeight) {

		// Получаем или создаем карту для события
		var usersToWeights = leadingWeightMatrix
				.computeIfAbsent(eventId, k -> new HashMap<>());

		// Получаем текущий вес пользователя
		Double currentLeadingWeight = usersToWeights.get(userId);

		// Если пользователь еще не взаимодействовал с событием
		if (currentLeadingWeight == null) {
			currentLeadingWeight = 0.0;
		}

		// Если веса одинаковые или новый меньше
		if (newLeadingWeight <= currentLeadingWeight) {
			log.info("Обновлять нечего");
			return false;
		}

		log.info("""
						Обновление ведущего веса мероприятия
						{
						    "userId": {},
						    "eventId": {},
						    "score": {}
						}""",
				userId, eventId, newLeadingWeight);

		// Заменяем старый ведущий вес на новый
		usersToWeights.put(userId, newLeadingWeight);

		// Обновляем сумму весов для события по всем пользователям
		updateSumOfLeadingWeights(eventId, currentLeadingWeight, newLeadingWeight);

		// Запускаем пересчет модели весов
		recalculateData(eventId, userId, currentLeadingWeight, newLeadingWeight);

		return true;
	}

	private void recalculateData(long eventA_Id,
	                             long userId,
	                             double oldLeadingWeight_A,
	                             double leadingWeight_A) {

		leadingWeightMatrix.forEach((eventB_Id, userIdToLeadingWeight) -> {

			// Пропускаем строку с событием А
			if (eventB_Id == eventA_Id) {
				return;
			}

			log.info("Пересчет модели весов для мероприятий {} и {}", eventA_Id, eventB_Id);

			// Получаем ведущий вес В из текущей строки
			Double leadingWeight_B = userIdToLeadingWeight.get(userId);
			if (leadingWeight_B == null) {
				return; // пользователь не взаимодействовал с этим событием
			}

			// Вычисляем старый минимальный вес среди ведущих
			double oldMinWeight = Math.min(oldLeadingWeight_A, leadingWeight_B);

			// Вычисляем новый минимальный вес среди ведущих
			double newMinWeight = Math.min(leadingWeight_A, leadingWeight_B);

			// Вычисляем старую сумму минимальных весов
			double oldSumOfMinWeight = getSumOfMinWeight(eventA_Id, eventB_Id);

			// Новая сумма минимальных весов = (старая сумма - старый минимальный вес) + новый минимальный вес
			double newSumOfMinWeight = (oldSumOfMinWeight - oldMinWeight) + newMinWeight;

			// Сетим новую сумму минимальных весов в матрицу вместо старой
			putNewSumOfMinWeight(eventA_Id, eventB_Id, newSumOfMinWeight);
		});

		log.info("Пересчет данных завершен");
	}

	@NonNull
	private List<EventsSimilarityAvro> calculateSimilarities(long eventA_Id, long userId) {

		Map<Long, Double> candidates = getCandidates(eventA_Id);
		if (candidates.isEmpty()) {
			log.info("В матрице сходства отсутствуют записи о событии {}", eventA_Id);
			return Collections.emptyList();
		}

		List<Long> eventIds = new ArrayList<>();
		leadingWeightMatrix.forEach((event_Id, userIdToLeadingWeight) -> {
			if (userIdToLeadingWeight.containsKey(userId)) {
				eventIds.add(event_Id);
			}
		});

		var similarities = new ArrayList<EventsSimilarityAvro>();
		candidates.forEach((eventB_Id, sumOfMinWeight) -> {

			if (eventB_Id == eventA_Id) {
				return;
			}
			if (!eventIds.contains(eventB_Id)) {
				return;
			}

			long eventA = Math.min(eventA_Id, eventB_Id);
			long eventB = Math.max(eventA_Id, eventB_Id);

			log.info("Рассчет сходства мероприятий {} и {}", eventA, eventB);

			double leadingWeightsSumByEventA = eventToLeadingWeightsSum.get(eventA);
			double leadingWeightsSumByEventB = eventToLeadingWeightsSum.get(eventB);
			double norm1 = Math.sqrt(leadingWeightsSumByEventA);
			double norm2 = Math.sqrt(leadingWeightsSumByEventB);

			double score = sumOfMinWeight / (norm1 * norm2);

			log.info("""
							Коэффициент подобия
							для мероприятий [{}] и [{}] равен [{}]
							(min=[{}], norm1=[{}],  norm2=[{}])
							""",
					eventA, eventB,
					(double) Math.round(score * 100) / 100.0,
					(double) Math.round(sumOfMinWeight * 100) / 100.0,
					(double) Math.round(norm1 * 100) / 100.0,
					(double) Math.round(norm2 * 100) / 100.0
			);

			similarities.add(
					EventsSimilarityAvro.newBuilder()
							.setEventA(eventA)
							.setEventB(eventB)
							.setScore(score)
							.setTimestamp(Instant.now())
							.build()
			);
		});

		log.info("Расчет сходства завершен");
		return similarities.stream().filter(sim -> sim.getScore() >= 0.005).toList();
	}

	private void sendSimilarity(long eventId, @NonNull EventsSimilarityAvro avro) {

		log.info("""
						Отправляю новое сходство
						{
							"eventA": {},
							"eventB": {},
							"score": {},
							"timestamp": {}
						}""",
				avro.getEventA(),
				avro.getEventB(),
				(double) Math.round(avro.getScore() * 100) / 100.0,
				TimestampUtils.toLocalDateTime(avro.getTimestamp())
		);

		CompletableFuture<RecordMetadata> result = producer.sendMessage(eventId, avro);

		result.whenComplete((record, throwable) -> {
			if (throwable != null) {
				log.error("Ошибка отправки действия пользователя {}:\n {}",
						avro, throwable.getMessage(), throwable);
			} else {
				log.info("Сходство отправлено по адресу:\n topic={}, key={}, partition={}, offset={}",
						record.topic(),
						avro.getEventA(),
						record.partition(),
						record.offset()
				);
			}
		});
	}

	// -------------------------------------UTILITY-------------------------------------

	private double calculateLeadingWeight(@NonNull ActionTypeAvro actionType) {
		return switch (actionType) {
			case VIEWS -> 0.4d;
			case REGISTER -> 0.8d;
			case LIKE -> 1.0d;
		};
	}

	/// Пересчет суммы ведущих весов текущего события по всем пользователям
	private void updateSumOfLeadingWeights(long eventId, double oldLeadingWeight, double newLeadingWeight) {
		eventToLeadingWeightsSum.compute(eventId, (k, oldSum) -> {

			double weight = oldSum == null
					? newLeadingWeight : (oldSum - oldLeadingWeight) + newLeadingWeight;

			log.info("""
							Обновление суммы ведущих весов события по всем пользователям
							{
								"eventId": {},
								"leadingWeightsSum": {}
							}""",
					eventId, (double) Math.round(weight * 100) / 100.0
			);

			return weight;
		});
	}

	@NonNull
	private Map<Long, Double> getCandidates(long eventA_Id) {
		Map<Long, Double> candidates = new HashMap<>();

		// Случай 1: eventA_Id — минимальный ключ (верхний треугольник)
		Map<Long, Double> upper = sumOfMinWeightsMatrix.get(eventA_Id);
		if (upper != null) {
			candidates.putAll(upper);
		}

		// Случай 2: eventA_Id — максимальный ключ (нижний треугольник)
		sumOfMinWeightsMatrix.forEach((first, row) -> {
			Double v = row.get(eventA_Id);
			if (v != null) {
				candidates.put(first, v);
			}
		});

		return candidates;
	}

	private void putNewSumOfMinWeight(long eventA, long eventB, double sum) {
		// Упорядочиваем идентификаторы чтобы не писать в симметричную половину
		long first = Math.min(eventA, eventB);
		long second = Math.max(eventA, eventB);

		sumOfMinWeightsMatrix
				.computeIfAbsent(first, e -> new HashMap<>())
				.put(second, sum);
	}

	private double getSumOfMinWeight(long eventA, long eventB) {
		long first = Math.min(eventA, eventB);
		long second = Math.max(eventA, eventB);

		return sumOfMinWeightsMatrix
				.computeIfAbsent(first, e -> new HashMap<>())
				.getOrDefault(second, 0.0);
	}

	// -------------------------------------LOGGING-------------------------------------

	private void printMatrix() {
		log.info(buildLeadingWeightMatrixTable());
		log.info(buildSumOfMinWeightsMatrixTable());
		log.info(buildEventToLeadingWeightsSumTable());
	}

	/**
	 * GENERATED BY DEEPSEEK</br>
	 */
	public String buildLeadingWeightMatrixTable() {
		if (leadingWeightMatrix.isEmpty()) {
			return TableRenderer.EMPTY_TABLE;
		}

		var eventIds = new TreeSet<>(leadingWeightMatrix.keySet());
		var userIds = new TreeSet<Long>();
		leadingWeightMatrix.values().forEach(m -> userIds.addAll(m.keySet()));

		List<String> columnNames = userIds.stream().map(id -> "u" + id).toList();
		List<String> rowNames = eventIds.stream().map(id -> "e" + id).toList();

		List<List<String>> values = new ArrayList<>();
		for (Long eventId : eventIds) {
			Map<Long, Double> userWeights = leadingWeightMatrix.get(eventId);
			List<String> row = new ArrayList<>();
			for (Long userId : userIds) {
				row.add(TableRenderer.formatValue(userWeights.get(userId)));
			}
			values.add(row);
		}

		return TableRenderer.renderTable("Матрица ведущих весов:", columnNames, rowNames, values);
	}

	/**
	 * GENERATED BY DEEPSEEK</br>
	 */
	public String buildSumOfMinWeightsMatrixTable() {
		if (sumOfMinWeightsMatrix.isEmpty()) {
			return TableRenderer.EMPTY_TABLE;
		}

		var eventIds = new TreeSet<Long>();
		sumOfMinWeightsMatrix.forEach((a, m) -> {
			eventIds.add(a);
			eventIds.addAll(m.keySet());
		});

		List<String> columnNames = eventIds.stream().map(id -> "e" + id).toList();
		List<String> rowNames = new ArrayList<>(columnNames);

		List<List<String>> values = new ArrayList<>();
		for (Long eventA : eventIds) {
			Map<Long, Double> candidates = getCandidates(eventA);
			List<String> row = new ArrayList<>();
			for (Long eventB : eventIds) {
				if (eventA.equals(eventB)) {
					row.add("");
				} else {
					row.add(TableRenderer.formatValue(candidates.get(eventB)));
				}
			}
			values.add(row);
		}

		return TableRenderer.renderTable("Матрица числителей:", columnNames, rowNames, values);
	}

	/**
	 * GENERATED BY DEEPSEEK</br>
	 */
	public String buildEventToLeadingWeightsSumTable() {
		if (eventToLeadingWeightsSum.isEmpty()) {
			return TableRenderer.EMPTY_TABLE;
		}

		var eventIds = new TreeSet<>(eventToLeadingWeightsSum.keySet());

		List<String> columnNames = eventIds.stream().map(id -> "e" + id).toList();
		List<String> rowNames = List.of("Сумма");

		List<List<String>> values = new ArrayList<>();
		List<String> row = new ArrayList<>();
		for (Long eventId : eventIds) {
			row.add(TableRenderer.formatValue(eventToLeadingWeightsSum.get(eventId)));
		}
		values.add(row);

		return TableRenderer.renderTable("Таблица знаминателей:", columnNames, rowNames, values);
	}

}

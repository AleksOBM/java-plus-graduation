package ru.practicum.ewm.stats.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.spi.Limit;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.analyzer.model.EventInteraction;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.repository.InteractionsRepository;
import ru.practicum.ewm.stats.analyzer.repository.SimilarityRepository;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationServiceImpl implements RecommendationService {

	InteractionsRepository interactionsRepository;
	SimilarityRepository similarityRepository;

	/**
	 * Сформировать персональные рекомендации для пользователя.
	 */
	@Override
	public Stream<RecommendedEventProto> getRecomendationsForUser(long userId, int maxResults) {

		// Выгрузить мероприятия, с которыми пользователь уже взаимодействовал
		Set<Long> userInteractedEvents = interactionsRepository
				.findInteractedEventIds(userId);

		// ЭТАП 1: Подбор мероприятий, с которыми пользователь ещё не взаимодействовал
		List<Long> candidates = findCandidates(userId, userInteractedEvents, maxResults);
		if (candidates.isEmpty()) {
			log.info("Нет новых мероприятий для пользователя с id={}", userId);
			return Stream.empty();
		}

		// ЭТАП 2: Вычисление предсказанной оценки для каждого нового мероприятия
		Map<Long, Double> predictedScores = predictScores(
				userId, candidates, userInteractedEvents, maxResults);
		if (predictedScores.isEmpty()) {
			log.info("Не удалось предсказать оценки для пользователя с id={}", userId);
			return Stream.empty();
		}

		/*
		 * Отсортировать найденные мероприятия по предсказанной оценке
		 * от большего к меньшему и выбрать первые N мероприятий.
		 */
		return predictedScores.entrySet().stream()
				.sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
				.limit(maxResults)
				.map(entry -> RecommendedEventProto.newBuilder()
						.setEventId(entry.getKey())
						.setScore(entry.getValue())
						.build());
	}

	/**
	 * ЭТАП 1. Подбор мероприятий, с которыми пользователь ещё не взаимодействовал.
	 */
	@NonNull
	private List<Long> findCandidates(long userId,
	                                  @NonNull Set<Long> userInteractedEvents,
	                                  int maxResults) {

		/*
		 * Получить недавно просмотренные. Выгрузить мероприятия, с которыми
		 * пользователь уже взаимодействовал, отсортировать по дате взаимодействия
		 * от новых к старым и ограничить N взаимодействиями.
		 */
		List<Long> recently = interactionsRepository
				.findRecentlyInteractions(userId, new Limit(0, maxResults));

		/*
		 * Если пользователь ещё не взаимодействовал ни с одним мероприятием,
		 * то рекомендовать нечего — возвращается пустой список.
		 */
		if (recently.isEmpty()) {
			log.info("Нет недавних взаимодействий у пользователя с id={}", userId);
			return List.of();
		}

		// Найти похожие новые мероприятия, исключая уже взаимодействованные
		return findNewCandidates(recently, userInteractedEvents, maxResults);
	}

	/**
	 * ЭТАП 2. Вычисление предсказанной оценки для каждого нового мероприятия.
	 */
	@NonNull
	private Map<Long, Double> predictScores(long userId,
	                                        @NonNull List<Long> candidates,
	                                        @NonNull Set<Long> userInteractedEvents,
	                                        int maxResults) {

		Map<Long, Double> predictedScores = new HashMap<>();

		for (Long candidateId : candidates) {

			/*
			 * Найти K ближайших соседей — просмотренных мероприятий, максимально
			 * похожих на предсказываемое. На основе их оценок будет предсказана новая.
			 */
			List<Long> nearbouses = findNearbouses(candidateId, userInteractedEvents, maxResults);
			if (nearbouses.isEmpty()) {
				continue;
			}

			// Получить оценки пользователя за ближайших соседей
			Set<EventInteraction> interactions = interactionsRepository
					.aggregateActionWeightByEventAndByUser(nearbouses, userId);
			if (interactions.isEmpty()) {
				continue;
			}

			// Получить коэффициенты подобия между кандидатом и его соседями
			Map<Long, Double> similarityByNearbour = similarityRepository
					.findSimilaritiesByEventId(candidateId).stream()
					.filter(sim -> {
						long other = sim.eventA() == candidateId
								? sim.eventB() : sim.eventA();
						return nearbouses.contains(other);
					})
					.collect(Collectors.toMap(
							sim -> sim.eventA() == candidateId
									? sim.eventB() : sim.eventA(),
							sim -> sim.score().doubleValue(),
							(a, b) -> a
					));

			/*
			 * Вычислить сумму взвешенных оценок: перемножить оценки мероприятий
			 * с их коэффициентами подобия и сложить все произведения.
			 *
			 * Вычислить сумму коэффициентов подобия: сложить все коэффициенты.
			 */
			double weightedSum = 0.0;
			double scoreSum = 0.0;

			for (EventInteraction interaction : interactions) {
				Double score = similarityByNearbour.get(interaction.eventId());
				if (score == null) {
					continue;
				}
				double weight = interaction.actionWeight().doubleValue();
				weightedSum += score * weight;
				scoreSum += score;
			}

			// если сумма коэффициентов равна нулю — пропустить
			if (scoreSum == 0.0) {
				continue;
			}

			// Вычислить оценку нового мероприятия
			predictedScores.put(candidateId, weightedSum / scoreSum);
		}

		return predictedScores;
	}

	/**
	 * Найти мероприятия, похожие на недавно просмотренные, с которыми пользователь
	 * ещё не взаимодействовал. Отсортировать по коэффициенту подобия от большего
	 * к меньшему и выбрать первые N мероприятий.
	 */
	@NonNull
	private List<Long> findNewCandidates(@NonNull List<Long> recently,
	                                     @NonNull Set<Long> userInteractedEvents,
	                                     int maxResults) {
		return similarityRepository.findSimilaritiesByManyEventIds(recently).stream()

				// Определить, какое из двух мероприятий пары является новым
				.map(sim -> recently.contains(sim.eventA())
						? sim.eventB() : sim.eventA())

				// Исключить мероприятия, с которыми пользователь уже взаимодействовал
				.filter(candidate -> !userInteractedEvents.contains(candidate))

				.distinct()
				.limit(maxResults)
				.toList();
	}

	/**
	 * Найти K ближайших соседей — просмотренных мероприятий, максимально похожих
	 * на предсказываемое. Важно найти именно максимально похожие мероприятия,
	 * с которыми пользователь уже взаимодействовал.
	 */
	@NonNull
	private List<Long> findNearbouses(long candidateId,
	                                  @NonNull Set<Long> userInteractedEvents,
	                                  int maxResults) {
		return similarityRepository.findSimilaritiesByEventId(candidateId).stream()

				// Отсортировать по коэффициенту подобия от большего к меньшему
				.sorted(Comparator.comparingDouble(
						(EventSimilarity sim) -> sim.score().doubleValue()).reversed())

				// Для каждой пары определить "соседа" — мероприятие, отличное от предсказываемого
				.map(sim -> sim.eventA() == candidateId
						? sim.eventB() : sim.eventA())

				// Исключить само предсказываемое мероприятие
				.filter(id -> !id.equals(candidateId))

				// Оставить только те мероприятия, с которыми пользователь уже взаимодействовал
				.filter(userInteractedEvents::contains)
				.distinct()
				.limit(maxResults)
				.toList();
	}

}
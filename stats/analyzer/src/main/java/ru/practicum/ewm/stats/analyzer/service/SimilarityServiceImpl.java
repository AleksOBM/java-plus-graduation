package ru.practicum.ewm.stats.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.analyzer.mapper.SimilarityMapper;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.repository.InteractionsRepository;
import ru.practicum.ewm.stats.analyzer.repository.SimilarityRepository;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SimilarityServiceImpl implements SimilarityService {

	SimilarityRepository repository;
	InteractionsRepository interactionsRepository;

	@Override
	public void addSimilarity(EventsSimilarityAvro record) {

		var similarity = SimilarityMapper.toEntity(record);

		log.info("""
						Сохраняю новое сходство событий:
						{
							"eventA": {},
							"eventB": {},
							"score": {},
							"timestamp": {}
						}
						""",
				similarity.getEventA(),
				similarity.getEventB(),
				similarity.getScore(),
				TimestampUtils.toLocalDateTime(record.getTimestamp())
		);

		repository.findByEventAAndEventB(similarity.getEventA(), similarity.getEventB())
				.ifPresentOrElse(sim -> {
							similarity.setId(sim.getId());
							repository.save(similarity);
						},
						() -> repository.save(similarity)
				);
	}

	@Override
	public Stream<RecommendedEventProto> getSimilarEvents(@NonNull SimilarEventsRequestProto request) {

		long eventId = request.getEventId();
		Set<Long> badInteractions = interactionsRepository
				.findInteractedEventIds(request.getUserId());

		return repository.findSimilaritiesByEventId(eventId).stream()
				.filter(sim -> !badInteractions
						.containsAll(List.of(sim.eventA(), sim.eventB())))
				.sorted(Comparator.comparingDouble(
						(EventSimilarity sim) -> sim.score().doubleValue()).reversed())
				.limit(request.getMaxResults())
				.map(sim -> RecommendedEventProto.newBuilder()
						.setEventId(sim.eventA() == eventId
								? sim.eventB() : sim.eventA())
						.setScore(sim.score().doubleValue())
						.build());
	}

}

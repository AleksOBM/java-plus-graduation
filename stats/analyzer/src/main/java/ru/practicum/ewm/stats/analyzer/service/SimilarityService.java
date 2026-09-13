package ru.practicum.ewm.stats.analyzer.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;

import java.util.stream.Stream;

public interface SimilarityService {

	@Transactional
	void addSimilarity(EventsSimilarityAvro record);

	Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);
}

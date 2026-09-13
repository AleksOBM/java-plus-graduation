package ru.practicum.ewm.stats.analyzer.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.util.Collection;
import java.util.stream.Stream;

public interface InteractionsService {

	@Transactional
	void addInteraction(UserActionAvro record);

	Stream<RecommendedEventProto> getInteractionsByEventIds(Collection<Long> eventIds);
}

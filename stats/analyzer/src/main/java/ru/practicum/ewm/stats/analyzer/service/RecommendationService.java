package ru.practicum.ewm.stats.analyzer.service;

import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.util.stream.Stream;

public interface RecommendationService {

	Stream<RecommendedEventProto> getRecomendationsForUser(long userId, int maxResults);
}

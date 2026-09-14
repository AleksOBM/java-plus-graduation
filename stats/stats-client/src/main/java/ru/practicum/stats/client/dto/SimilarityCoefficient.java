package ru.practicum.stats.client.dto;

import lombok.Builder;

@Builder
public record SimilarityCoefficient(
		long eventId,
		double score
) {
}

package ru.practicum.stats.client.dto;

import lombok.Builder;

@Builder
public record ActionsWeightsSum(
		long eventId,
		double score
) {
}

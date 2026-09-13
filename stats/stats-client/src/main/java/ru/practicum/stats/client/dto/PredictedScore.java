package ru.practicum.stats.client.dto;

import lombok.Builder;

/**
 * Предсказание о возможном понравившемся событии
 */
@Builder
public record PredictedScore(
		long eventId,
		double score
) {
}

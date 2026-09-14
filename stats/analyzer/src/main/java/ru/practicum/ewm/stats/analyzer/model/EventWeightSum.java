package ru.practicum.ewm.stats.analyzer.model;

import java.math.BigDecimal;

public record EventWeightSum(
		long eventId,
		BigDecimal score
) {
}

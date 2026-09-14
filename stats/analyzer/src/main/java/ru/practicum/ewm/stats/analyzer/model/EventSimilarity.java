package ru.practicum.ewm.stats.analyzer.model;

import java.math.BigDecimal;

public record EventSimilarity(
		long eventA,
		long eventB,
		BigDecimal score
) {
}

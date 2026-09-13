package ru.practicum.ewm.stats.analyzer.model;

import java.math.BigDecimal;

public record EventInteraction(
		long userId,
		long eventId,
		BigDecimal actionWeight
) {
}

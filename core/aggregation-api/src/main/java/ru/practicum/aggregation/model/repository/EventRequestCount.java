package ru.practicum.aggregation.model.repository;

import lombok.Builder;

/**
 * @apiNote
 * {@link Long} eventId <br/>
 * {@link Integer} count
 */
@Builder
public record EventRequestCount(
		Long eventId,
		Long count
) {
}

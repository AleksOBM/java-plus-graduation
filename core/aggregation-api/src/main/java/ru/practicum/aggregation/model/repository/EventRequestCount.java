package ru.practicum.aggregation.model.repository;

/**
 * @apiNote
 * {@link Long} eventId <br/>
 * {@link Integer} count
 */
public record EventRequestCount(
		Long eventId,
		Long count
) {
}

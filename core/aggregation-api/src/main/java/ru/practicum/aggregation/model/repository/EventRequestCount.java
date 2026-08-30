package ru.practicum.aggregation.model.repository;
/**
 * @apiNote
 * {@link Long} eventId <br/>
 * {@link Long} count
 */
public record EventRequestCount(
		Long eventId,
		Long count
) {
}

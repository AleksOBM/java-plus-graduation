package ru.practicum.aggregation.model.data;

import lombok.Builder;
import ru.practicum.aggregation.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @param users
 * @param states
 * @param categories
 * @param rangeStart
 * @param rangeEnd
 * @param from
 * @param size
 */
@Builder
public record AdminGetData(
		List<Integer> users,
		List<EventState> states,
		List<Integer> categories,
		LocalDateTime rangeStart,
		LocalDateTime rangeEnd,
		Integer from,
		Integer size
) {
}
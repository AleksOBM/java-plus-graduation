package ru.practicum.aggregation.dto.event.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @param text
 * @param categories
 * @param paid
 * @param rangeStart
 * @param rangeEnd
 * @param onlyAvailable
 * @param sort
 * @param from
 * @param size
 */
@NotNull
@Builder
public record FreeGetDto(
		String text,
		List<Integer> categories,
		Boolean paid,
		LocalDateTime rangeStart,
		LocalDateTime rangeEnd,
		Boolean onlyAvailable,
		FreeEventSort sort,
		Integer from,
		Integer size
) {

	public enum FreeEventSort {
		EVENT_DATE, VIEWS
	}
}

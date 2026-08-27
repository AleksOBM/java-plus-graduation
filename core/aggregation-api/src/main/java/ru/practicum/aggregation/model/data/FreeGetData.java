package ru.practicum.aggregation.model.data;

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
public record FreeGetData(
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

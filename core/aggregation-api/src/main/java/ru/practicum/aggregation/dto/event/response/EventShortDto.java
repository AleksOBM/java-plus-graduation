package ru.practicum.aggregation.dto.event.response;

import lombok.Builder;
import ru.practicum.aggregation.dto.category.CategoryDto;
import ru.practicum.aggregation.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Builder
public record EventShortDto(
		long id,
		String annotation,
		CategoryDto category,
		long confirmedRequests,
		LocalDateTime eventDate,
		UserShortDto initiator,
		boolean paid,
		String title,
		long views,
		long rate
) {
}

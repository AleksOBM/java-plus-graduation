package ru.practicum.aggregation.dto.event.come.update;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.practicum.aggregation.enums.UserStateAction;
import ru.practicum.aggregation.model.entity.Location;

import java.time.LocalDateTime;

@Builder
public record UpdateEventUserRequest(

		@Size(min = 20, max = 2000)
		String annotation,

		Long category,

		@Size(min = 20, max = 7000)
		String description,

		LocalDateTime eventDate,

		Location location,

		Boolean paid,

		Integer participantLimit,

		Boolean requestModeration,

		UserStateAction stateAction,

		@Size(min = 3, max = 120)
		String title
) {
}

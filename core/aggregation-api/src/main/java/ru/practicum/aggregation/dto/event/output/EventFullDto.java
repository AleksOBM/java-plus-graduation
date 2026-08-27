package ru.practicum.aggregation.dto.event.output;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.aggregation.dto.category.output.CategoryDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.aggregation.model.entity.Location;

import java.time.LocalDateTime;

/**
 * @apiNote <code>Long id</code><br/>
 * <code>String annotation</code><br/>
 * <code>CategoryDto category</code><br/>
 * <code>Long confirmedRequests</code><br/>
 * <code>LocalDateTime createdOn</code><br/>
 * <code>String description</code><br/>
 * <code>LocalDateTime eventDate</code><br/>
 * <code>UserShortDto initiator</code><br/>
 * <code>Location location</code><br/>
 * <code>boolean paid </code><br/>
 * <code>Integer participantLimit</code><br/>
 * <code>LocalDateTime publishedOn</code><br/>
 * <code>boolean requestModeration</code><br/>
 * <code>EventState state</code><br/>
 * <code>String title</code><br/>
 * <code>Long views</code><br/>
 * <code>long rate</code>
 */
@Builder
public record EventFullDto(

		Long id,

		@NotBlank
		String annotation,

		@NotNull
		CategoryDto category,

		Long confirmedRequests,

		LocalDateTime createdOn,

		String description,

		@NotNull
		LocalDateTime eventDate,

		@NotNull
		UserShortDto initiator,

		@NotNull
		Location location,

		boolean paid,

		Integer participantLimit,

		LocalDateTime publishedOn,

		boolean requestModeration,

		EventState state,

		@NotBlank
		String title,

		Long views,

		long rate
) {
}
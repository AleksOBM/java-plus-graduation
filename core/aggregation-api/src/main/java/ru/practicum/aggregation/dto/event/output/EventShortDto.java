package ru.practicum.aggregation.dto.event.output;

import lombok.Builder;
import ru.practicum.aggregation.dto.category.output.CategoryDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

import java.time.LocalDateTime;

/**
 * @apiNote
 * <code>long</code> id<br/>
 * {@link String} annotation<br/>
 * {@link CategoryDto} category<br/>
 * <code>long</code> confirmedRequests<br/>
 * {@link LocalDateTime} eventDate<br/>
 * {@link UserShortDto} initiator<br/>
 * <code>boolean</code> paid<br/>
 * {@link String} title<br/>
 * <code>double</code> rateing
 */
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
		double rating
) {
}

package ru.practicum.aggregation.dto.rating;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

/**
 * @apiNote
 * <code>{@link Long} eventId</code> <br/>
 * <code>{@link Long} rate</code>
 */
@Builder
public record RatingUpdateRequest(

		@Positive
		Long eventId,

		@NotNull
		Long rate

) {
}

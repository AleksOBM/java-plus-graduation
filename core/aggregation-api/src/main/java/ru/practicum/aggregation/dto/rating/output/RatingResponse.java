package ru.practicum.aggregation.dto.rating.output;

import lombok.Builder;
import ru.practicum.aggregation.enums.Reaction;

/**
 * @apiNote
 * <code>Long</code> id <br/>
 * <code>Long</code> userId<br/>
 * <code>Long</code> eventId<br/>
 * {@link Reaction} reaction
 */
@Builder
public record RatingResponse(
		Long id,
		Long userId,
		Long eventId,
		Reaction reaction
) {
}

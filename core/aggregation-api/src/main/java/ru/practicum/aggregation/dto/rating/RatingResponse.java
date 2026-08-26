package ru.practicum.aggregation.dto.rating;

import lombok.Builder;
import ru.practicum.aggregation.enums.Reaction;

@Builder
public record RatingResponse(
		Long id,
		Long userId,
		Long eventId,
		Reaction reaction
) {
}

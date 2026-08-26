package ru.practicum.aggregation.dto.rating;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.aggregation.enums.Reaction;

@Builder
public record RatingRequest(

		@NotNull
		Reaction reaction
) {
}

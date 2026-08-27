package ru.practicum.aggregation.dto.rating.come.create;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.aggregation.enums.Reaction;

@Builder
public record RatingCreateRequest(

		@NotNull
		Reaction reaction
) {
}

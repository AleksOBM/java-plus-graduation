package ru.practicum.aggregation.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @param name
 */
public record NewCategoryDto(

		@NotBlank
		@Size(min = 1, max = 50)
		String name
) {
}

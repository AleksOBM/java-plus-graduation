package ru.practicum.aggregation.dto.category.output;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Данные категории для изменения категории
 * @apiNote
 * <code>Long</code> id <br/>
 * <code>String</code> name
 */
@Builder
public record CategoryDto(

		Long id,

		@NotBlank
		@Size(min = 1, max = 50)
		String name
) {
}

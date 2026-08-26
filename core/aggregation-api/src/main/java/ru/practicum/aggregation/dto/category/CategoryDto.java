package ru.practicum.aggregation.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
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

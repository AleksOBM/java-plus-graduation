package ru.practicum.aggregation.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * @apiNote Long id <br/>
 * String name <br/>
 * String email
 */
@Builder
public record UserShortDto(

		Long id,

		@NotBlank
		String name,

		@Email
		@Size(min = 6, max = 255)
		@NotBlank
		String email
) {
}

package ru.practicum.aggregation.dto.user;

import lombok.Builder;

/**
 * @apiNote
 * Long id <br/>
 * String name <br/>
 * String email
 */
@Builder
public record UserDto(
		Long id,
		String name,
		String email
) {
}

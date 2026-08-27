package ru.practicum.aggregation.dto.user.output;

import lombok.Builder;

/**
 * @apiNote
 * {@link Long} id <br/>
 * {@link String} name <br/>
 * {@link String} email
 */
@Builder
public record UserDto(
		Long id,
		String name,
		String email
) {
}

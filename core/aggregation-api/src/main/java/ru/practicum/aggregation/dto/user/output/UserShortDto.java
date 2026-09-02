package ru.practicum.aggregation.dto.user.output;

import lombok.Builder;

/**
 * @apiNote
 * <code>Long</code> id <br/>
 * <code>String</code> name
 */
@Builder
public record UserShortDto(
		Long id,
		String name
) {
}

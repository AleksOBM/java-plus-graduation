package ru.practicum.aggregation.dto.compilation.output;

import lombok.Builder;
import ru.practicum.aggregation.dto.event.output.EventShortDto;

import java.util.List;

/**
 * @apiNote
 * <code>Long</code> id <br/>
 * <code>boolean</code> pinned <br/>
 * <code>String</code> title <br/>
 * <code>List<</code>{@link EventShortDto}<code>></code> events
 */
@Builder
public record CompilationDto(
		Long id,
		boolean pinned,
		String title,
		List<EventShortDto> events
) {
}

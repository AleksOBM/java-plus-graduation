package ru.practicum.aggregation.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.aggregation.dto.event.response.EventShortDto;

import java.util.List;

/**
 * <code>Long</code> id <br/>
 * <code>boolean</code> pinned <br/>
 * <code>String</code> title <br/>
 * <code>List<{@link EventShortDto}></code> events
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
	private Long id;
	private boolean pinned;
	private String title;
	private List<EventShortDto> events;
}

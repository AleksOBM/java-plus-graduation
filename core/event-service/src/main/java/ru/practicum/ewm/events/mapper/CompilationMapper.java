package ru.practicum.ewm.events.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.compilation.CompilationDto;
import ru.practicum.aggregation.dto.compilation.NewCompilationDto;
import ru.practicum.aggregation.dto.user.UserShortDto;
import ru.practicum.ewm.events.entity.Compilation;
import ru.practicum.ewm.events.entity.Event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

	public CompilationDto toCompilationDto(@NonNull Compilation compilation,
	                                       @NonNull Map<Long, UserShortDto> eventIdToInitiator,
	                                       @NonNull Map<Long, Long> confirmedRequests,
	                                       @NonNull Map<Long, Long> views) {
		return CompilationDto.builder()
				.id(compilation.getId())
				.pinned(compilation.isPinned())
				.title(compilation.getTitle())
				.events(compilation.getEvents().stream()
						.map(event -> EventMapper.toEventShortDto(
								event,
								eventIdToInitiator.get(event.getId()),
								confirmedRequests.getOrDefault(event.getId(), 0L),
								views.getOrDefault(event.getId(), 0L)
						))
						.toList())
				.build();
	}

	public Compilation toEntity(@NonNull NewCompilationDto dto, Set<Event> events) {
		return Compilation.builder()
				.title(dto.getTitle())
				.pinned(dto.isPinned())
				.events(events != null ? events : new HashSet<>())
				.build();
	}

}

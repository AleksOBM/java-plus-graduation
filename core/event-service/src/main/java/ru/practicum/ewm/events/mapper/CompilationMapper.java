package ru.practicum.ewm.events.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.compilation.output.CompilationDto;
import ru.practicum.aggregation.dto.compilation.come.create.NewCompilationDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.ewm.events.entity.Compilation;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.model.EventData;

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
								EventData.builder()
										.initiator(eventIdToInitiator.get(event.getId()))
										.confirmedRequests(confirmedRequests
												.getOrDefault(event.getId(), 0L))
										.views(views.getOrDefault(event.getId(), 0L))
										.build()
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

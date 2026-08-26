package ru.practicum.ewm.events.service.compilation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.compilation.CompilationDto;
import ru.practicum.aggregation.dto.compilation.CompilationSearchFilter;
import ru.practicum.aggregation.dto.compilation.CompilationUpdateDto;
import ru.practicum.aggregation.dto.compilation.NewCompilationDto;
import ru.practicum.aggregation.dto.request.EventRequestCount;
import ru.practicum.aggregation.dto.user.UserShortDto;
import ru.practicum.aggregation.error.exception.NotFoundException;
import ru.practicum.aggregation.model.BaseEntity;
import ru.practicum.aggregation.repository.RequestFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepository;
import ru.practicum.ewm.events.mapper.CompilationMapper;
import ru.practicum.ewm.events.entity.Compilation;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.repository.CompilationRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.statistic.StatRestRepository;
import ru.practicum.stat.dto.ViewStatsDto;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationServiceImpl implements CompilationService {

	EventRepository eventRepository;
	CompilationRepository compilationRepository;

	StatRestRepository statRestRepository;

	UserFeignRepository userFeignRepository;
	RequestFeignRepository requestFeignRepository;

	@Override
	public CompilationDto getById(Long compilationId, HttpServletRequest request) {
		statRestRepository.sendHitRequest(request);

		var compilation = getCompilationById(compilationId);
		var confirmedRequests = getConfirmedRequests(List.of(compilation));
		var views = getViews(List.of(compilation));
		var events = compilation.getEvents();
		var eventIdToInitiator = eventIdToInitiatorMap(events);

		return CompilationMapper.toCompilationDto(compilation, eventIdToInitiator, confirmedRequests, views);
	}

	@Override
	public void delById(Long compilationId) {
		getCompilationById(compilationId);
		compilationRepository.deleteById(compilationId);
	}

	@Override
	public CompilationDto addCompilation(@NonNull NewCompilationDto compilationDto) {
		Set<Event> events = new HashSet<>();

		if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
			events = new HashSet<>(eventRepository.findAllById(compilationDto.getEvents()));

			if (events.size() < compilationDto.getEvents().size()) {
				throw new NotFoundException("Одно или несколько событий не найдены");
			}
		}

		var savedCompilation = compilationRepository.save(CompilationMapper.toEntity(compilationDto, events));

		Map<Long, Long> confirmedRequests = new HashMap<>();
		savedCompilation.getEvents().forEach(event -> confirmedRequests.put(event.getId(), 0L));

		Map<Long, Long> views = new HashMap<>();
		savedCompilation.getEvents().forEach(event -> views.put(event.getId(), 0L));

		var eventIdToInitiator = eventIdToInitiatorMap(events);

		return CompilationMapper.toCompilationDto(
				savedCompilation, eventIdToInitiator, confirmedRequests, views);
	}

	@Override
	public CompilationDto updateCompilation(Long compilationId,
	                                        @NonNull CompilationUpdateDto compilationUpdateDto) {
		var compilationInDb = getCompilationById(compilationId);

		if (compilationUpdateDto.getEvents() != null) {
			List<Event> eventsUpdate = new ArrayList<>();

			if (!compilationUpdateDto.getEvents().isEmpty()) {
				eventsUpdate = eventRepository.findAllById(compilationUpdateDto.getEvents());
				if (eventsUpdate.size() < compilationUpdateDto.getEvents().size()) {
					throw new NotFoundException("Одно или несколько событий не найдены");
				}
			}
			compilationInDb.setEvents(new HashSet<>(eventsUpdate));
		}

		if (compilationUpdateDto.getTitle() != null && !compilationUpdateDto.getTitle().isBlank()) {
			compilationInDb.setTitle(compilationUpdateDto.getTitle());
		}

		if (compilationUpdateDto.getPinned() != null) {
			compilationInDb.setPinned(compilationUpdateDto.getPinned());
		}

		var confirmedRequests = getConfirmedRequests(List.of(compilationInDb));
		var views = getViews(List.of(compilationInDb));
		var compilation = getCompilationById(compilationId);
		var events = compilation.getEvents();
		var eventIdToInitiator = eventIdToInitiatorMap(events);

		return CompilationMapper.toCompilationDto(
				compilationInDb, eventIdToInitiator, confirmedRequests, views);
	}

	@NonNull
	private Map<Long, UserShortDto> eventIdToInitiatorMap(@NonNull Set<Event> events) {
		var initiatorIds = events.stream().map(Event::getInitiatorId).toList();
		var initiatorMap = userFeignRepository.getUsersByIds(initiatorIds)
				.stream()
				.collect(Collectors.toMap(UserShortDto::id, Function.identity()));

		return events.stream()
				.collect(Collectors.toMap(BaseEntity::getId, e ->
								initiatorMap.get(e.getInitiatorId())
						)
				);
	}

	@Override
	public List<CompilationDto> getByFilter(@NonNull CompilationSearchFilter filter,
	                                        HttpServletRequest request) {
		var pageable = PageRequest.of(filter.getFrom() / filter.getSize(), filter.getSize());
		Page<Compilation> compilationsPage;

		if (filter.getPinned() != null) {
			compilationsPage = compilationRepository.findAllByPinned(filter.getPinned(), pageable);
		} else {
			compilationsPage = compilationRepository.findAll(pageable);
		}

		List<Compilation> compilations = compilationsPage.getContent();

		if (compilations.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Long, Long> allConfirmedRequests = getConfirmedRequests(compilations);
		Map<Long, Long> allViews = getViews(compilations);

		var result = new ArrayList<CompilationDto>();
		for (Compilation compilation : compilations) {

			var events = compilation.getEvents();
			var eventIdToInitiator = eventIdToInitiatorMap(events);

			result.add(CompilationMapper.toCompilationDto(
					compilation, eventIdToInitiator, allConfirmedRequests, allViews));
		}

		return result;
	}

	@NonNull
	private Compilation getCompilationById(long compilationId) {
		return compilationRepository.findById(compilationId).orElseThrow(
				() -> new NotFoundException("Подборка с id=" + compilationId + " не найдена")
		);
	}

	/// Map<eventId, confirmedRequests>
	@NonNull
	private Map<Long, Long> getConfirmedRequests(@NonNull Collection<Compilation> compilations) {
		List<Event> events = compilations.stream()
				.flatMap(c -> c.getEvents().stream())
				.toList();

		if (events.isEmpty()) return Collections.emptyMap();

		List<EventRequestCount> eventRequestCountList = requestFeignRepository
				.getConfirmedRequestsCount(events.stream().map(Event::getId).toList());

		return eventRequestCountList.stream()
				.collect(Collectors.toMap(
								EventRequestCount::getEventId,
								EventRequestCount::getCount,
								(existing, replacement) -> existing
						)
				);
	}

	/// Map<eventId, views>
	@NonNull
	private Map<Long, Long> getViews(@NonNull Collection<Compilation> compilations) {

		// Все уникальные события
		List<Event> allEvents = compilations.stream()
				.flatMap(c -> c.getEvents().stream())
				.distinct()
				.toList();

		if (allEvents.isEmpty()) return Collections.emptyMap();

		// Все URIs
		List<String> uris = allEvents.stream().map(e -> "/events/" + e.getId()).toList();

		List<ViewStatsDto> stats = statRestRepository.getStat(uris, false);

		//  Map<eventId, hits>
		return stats.stream().collect(Collectors.toMap(statsDto ->
								Long.parseLong(statsDto.getUri().replace("/events/", "")),
						ViewStatsDto::getHits,
						(a, b) -> a
				)
		);
	}

}

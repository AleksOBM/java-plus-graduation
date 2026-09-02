package ru.practicum.ewm.events.service.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.event.come.create.NewEventDto;
import ru.practicum.aggregation.dto.event.come.update.UpdateEventAdminRequest;
import ru.practicum.aggregation.dto.event.come.update.UpdateEventUserRequest;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.event.output.EventShortDto;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.aggregation.enums.AdminStateAction;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.aggregation.enums.UserStateAction;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.model.data.AdminGetData;
import ru.practicum.aggregation.model.data.FreeGetData;
import ru.practicum.aggregation.model.data.StatsRequestData;
import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.aggregation.repository.RequestFeignRepositoryImpl;
import ru.practicum.aggregation.repository.StatsFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepositoryImpl;
import ru.practicum.ewm.events.entity.Category;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.mapper.StateMapper;
import ru.practicum.ewm.events.mapper.UserMapper;
import ru.practicum.ewm.events.model.EventData;
import ru.practicum.ewm.events.repository.CategoryRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.specification.EventSpecifications;
import ru.practicum.ewm.events.specification.SpecBuilder;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {

	StatsFeignRepository statsFeignRepository;
	EventRepository eventRepository;
	CategoryRepository categoryRepository;

	UserFeignRepositoryImpl userFeignRepository;
	RequestFeignRepositoryImpl requestFeignRepository;

	private static final String EVENTS_PATH = "/events/";
	private static final int HOURS_BEFORE_START = 2;

	@Override
	public List<EventShortDto> getFreeEvents(@NonNull FreeGetData dto, HttpServletRequest request) {

		if (dto.rangeStart() != null && dto.rangeEnd() != null) {
			if (dto.rangeEnd().isBefore(dto.rangeStart())) {
				throw new ValidationException("Окончание события не может быть раньше начала");
			}
		}

		SpecBuilder<Event> builder = SpecBuilder.<Event>builder()
				.and(EventSpecifications.isPublished())
				.andIf(dto.text() != null && !dto.text().isBlank(),
						() -> EventSpecifications.textContains(dto.text()))
				.andIf(dto.categories() != null && !dto.categories().isEmpty(),
						() -> EventSpecifications.hasCategories(dto.categories()))
				.andIf(dto.paid() != null,
						() -> EventSpecifications.isPaid(dto.paid()))
				.andIf(Boolean.TRUE.equals(dto.onlyAvailable()),
						() -> EventSpecifications.onlyAvailable(true));

		boolean hasStart = dto.rangeStart() != null;
		boolean hasEnd = dto.rangeEnd() != null;

		if (!hasStart && !hasEnd) {
			builder.and(EventSpecifications.eventDateAfterNow(LocalDateTime.now()));
		} else {
			builder
					.andIf(hasStart,
							() -> EventSpecifications.dateAfter(dto.rangeStart()))
					.andIf(hasEnd,
							() -> EventSpecifications.dateBefore(dto.rangeEnd()));
		}

		Specification<Event> spec = builder.build();

		Sort sort = Sort.unsorted();

		if (dto.sort() != null) {
			switch (dto.sort()) {
				case EVENT_DATE -> sort = Sort.by("eventDate").ascending();
				case VIEWS -> sort = Sort.by("views").descending();
			}
		}

		Pageable pageable = PageRequest.of((dto.from() / dto.size()), dto.size(), sort);

		List<Event> events = eventRepository.findAll(spec, pageable).getContent();
		if (events.isEmpty()) {
			return Collections.emptyList();
		}

		var confirmedRequestsCounts = fetchConfirmedRequestsCount(
				events.stream().map(Event::getId).toList());

		Map<Long, Long> eventIdToRequestCount = getRequestCountMap(confirmedRequestsCounts);

		var statsData = StatsRequestData.builder()
				.uris(getUris(events))
				.start(dto.rangeStart())
				.end(dto.rangeEnd())
				.unique(false)
				.build();

		var statsOptional = statsFeignRepository.getStatList(statsData);

		Map<Long, UserShortDto> userIdToInitiator = getUserShortDtoMap(events);

		return events.stream()
				.map(event -> {
					var userId = event.getInitiatorId();
					var confirmets = eventIdToRequestCount.getOrDefault(userId, 0L);
					AtomicLong views = new AtomicLong();
					statsOptional.ifPresentOrElse(stats ->
									views.set(getViewsFromStatsList(stats, event.getId())),
							() -> views.set(1L)
					);
					var eventData = EventData.builder()
							.initiator(userIdToInitiator.get(userId))
							.confirmedRequests(confirmets)
							.views(views.get())
							.build();
					return EventMapper.toEventShortDto(event, eventData);
				})
				.toList();
	}

	@Override
	public EventFullDto getFreeEventById(Long eventId, @NonNull HttpServletRequest request) {
		checkEvent(eventId);

		var event = getEventById(eventId);
		var initiator = UserMapper.toUserShortDto(getUserById(event.getInitiatorId()));
		var uris = List.of(request.getRequestURI());
		var stats = statsFeignRepository.getStatList(
				StatsRequestData.builder()
						.uris(uris)
						.unique(true)
						.build()
		);

		long views = stats.map(viewStatsDtos -> viewStatsDtos.stream()
						.findFirst()
						.map(ViewStatsDto::getHits)
						.orElse(0L)
				)
				.orElse(1L);

		var confirmedRequestsCount = getConfirmedRequestsCountByEventId(eventId);

		var eventData = EventData.builder()
				.initiator(initiator)
				.confirmedRequests(confirmedRequestsCount)
				.views(views)
				.build();

		return EventMapper.toEventFullDto(event, eventData);
	}

	@Override
	public EventFullDto userAddNewEvent(Long userId, @NonNull NewEventDto newEventDto) {
		if (newEventDto.participantLimit() != null && newEventDto.participantLimit() < 0) {
			throw new ValidationException(
					"Ограничение на количество участников должно быть положительным числом");
		}

		if (newEventDto.eventDate() != null
				&& newEventDto.eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
			throw new ValidationException(
					"Начало события не может быть раньше, чем через два часа от текущего момента");
		}

		userFeignRepository.checkUser(userId);

		var category = getCategoryById(newEventDto.category());
		var event = eventRepository.save(
				EventMapper.toEntity(
						newEventDto,
						category,
						LocalDateTime.now(),
						userId,
						null,
						EventState.PENDING
				)
		);
		var initiator = UserMapper.toUserShortDto(getUserById(event.getInitiatorId()));

		var eventData = EventData.builder()
				.initiator(initiator)
				.confirmedRequests(0)
				.views(0)
				.build();

		return EventMapper.toEventFullDto(event, eventData);
	}

	@Override
	public List<EventFullDto> adminGetEvents(@NonNull AdminGetData dto) {
		Specification<Event> spec = SpecBuilder.<Event>builder()
				.andIf(dto.users() != null && !dto.users().isEmpty(),
						() -> EventSpecifications.hasUsers(dto.users()))
				.andIf(dto.states() != null && !dto.states().isEmpty(),
						() -> EventSpecifications.hasStates(dto.states()))
				.andIf(dto.categories() != null && !dto.categories().isEmpty(),
						() -> EventSpecifications.hasCategories(dto.categories()))
				.andIf(dto.rangeStart() != null,
						() -> EventSpecifications.dateAfter(dto.rangeStart()))
				.andIf(dto.rangeEnd() != null,
						() -> EventSpecifications.dateBefore(dto.rangeEnd()))
				.build();

		Pageable pageable = PageRequest.of(
				dto.from() / dto.size(),
				dto.size()
		);

		List<Event> events = eventRepository.findAll(spec, pageable).getContent();
		if (events.isEmpty()) {
			return Collections.emptyList();
		}

		List<EventRequestCount> confirmedRequestsCounts = fetchConfirmedRequestsCount(
				events.stream().map(Event::getId).toList());

		Map<Long, Long> eventIdToRequestCount = getRequestCountMap(confirmedRequestsCounts);

		var statsData = StatsRequestData.builder()
				.uris(getUris(events))
				.start(dto.rangeStart())
				.end(dto.rangeEnd())
				.unique(false)
				.build();

		var statsOptional = statsFeignRepository.getStatList(statsData);

		Map<Long, UserShortDto> initiators = getUserShortDtoMap(events);

		return events.stream()
				.map(event -> {
					var userId = event.getInitiatorId();
					var count = getConfirmedRequestsCountFromMap(eventIdToRequestCount, event.getId());
					AtomicLong views = new AtomicLong();
					statsOptional.ifPresentOrElse(stats ->
									views.set(getViewsFromStatsList(stats, event.getId())),
							() -> views.set(1L)
					);
					var eventData = EventData.builder()
							.initiator(initiators.get(userId))
							.confirmedRequests(count)
							.views(views.get())
							.build();
					return EventMapper.toEventFullDto(event, eventData);
				})
				.toList();
	}

	@Override
	public EventFullDto adminUpdateEvent(Long eventId, @NonNull UpdateEventAdminRequest request) {
		Event oldEvent = getEventById(eventId);
		Event newEvent;

		if (request.eventDate() != null && request.eventDate()
				.isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_START))) {
			throw new ValidationException(
					"Дата события не может быть раньше, чем через два часа от текущего момента");
		}

		if (request.stateAction() == null) {
			newEvent = EventMapper.update(
					oldEvent,
					request,
					oldEvent.getState(),
					oldEvent.getPublishedOn(),
					request.category() == null ?
							Optional.empty() :
							Optional.of(getCategoryById(request.category()))
			);
		} else {

			if (request.stateAction().equals(AdminStateAction.PUBLISH_EVENT) &&
					oldEvent.getState().equals(EventState.PUBLISHED)) {
				throw new ConflictException("Событие с id=%s уже опубликовано"
						.formatted(oldEvent.getId())
				);
			}

			if (request.stateAction().equals(AdminStateAction.PUBLISH_EVENT) &&
					oldEvent.getState().equals(EventState.CANCELED)) {
				throw new ConflictException("Публикация события с id=%s уже отменена пользователем"
						.formatted(oldEvent.getId())
				);
			}

			if (request.stateAction().equals(AdminStateAction.REJECT_EVENT) &&
					oldEvent.getState().equals(EventState.PUBLISHED)) {
				throw new ConflictException("Событие с id=%s уже опубликовано, отмена не возможна"
						.formatted(oldEvent.getId())
				);
			}

			newEvent = EventMapper.update(
					oldEvent,
					request,
					request.stateAction().equals(AdminStateAction.REJECT_EVENT)
							? EventState.CANCELED : EventState.PUBLISHED,
					request.stateAction().equals(AdminStateAction.REJECT_EVENT)
							? null : LocalDateTime.now(),
					request.category() == null
							? Optional.empty() : Optional.of(getCategoryById(request.category()))
			);
		}

		var event = eventRepository.save(newEvent);

		return EventMapper.toEventFullDto(event, getEventData(eventId, event.getInitiatorId()));
	}

	@Override
	public List<EventShortDto> findByUserId(Long userId, Integer from, Integer size) {
		userFeignRepository.checkUser(userId);

		PageRequest pageRequest = PageRequest.of(from / size, size);
		List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);
		if (events.isEmpty()) {
			return Collections.emptyList();
		}

		List<EventRequestCount> confirmedRequestsCounts = fetchConfirmedRequestsCount(
				events.stream().map(Event::getId).toList());

		Map<Long, Long> eventIdToRequestCount = getRequestCountMap(confirmedRequestsCounts);

		List<String> uris = getUris(events);
		var statsOptional = statsFeignRepository.getStatList(
				StatsRequestData.builder()
						.uris(uris)
						.unique(true)
						.build()
		);

		Map<Long, UserShortDto> initiators = getUserShortDtoMap(events);

		return events.stream()
				.map(event -> {
					var count = getConfirmedRequestsCountFromMap(eventIdToRequestCount, event.getId());
					AtomicLong views = new AtomicLong();
					statsOptional.ifPresentOrElse(stats ->
									views.set(getViewsFromStatsList(stats, event.getId())),
							() -> views.set(1L)
					);
					var eventData = EventData.builder()
							.initiator(initiators.get(userId))
							.confirmedRequests(count)
							.views(views.get())
							.build();
					return EventMapper.toEventShortDto(event, eventData);
				})
				.toList();
	}

	@Override
	public EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId) {
		userFeignRepository.checkUser(userId);

		Event event = getEventById(eventId);
		var initiatorId = event.getInitiatorId();

		if (!initiatorId.equals(userId)) {
			throw new ConflictException("Пользователь должен быть инициатором");
		}

		return EventMapper.toEventFullDto(event, getEventData(eventId, initiatorId));
	}

	@Override
	public EventFullDto findEventById(long eventId, long confirmets) {
		Event event = getEventById(eventId);
		var eventData = EventData.builder()
				.initiator(UserMapper.toUserShortDto(getUserById(event.getInitiatorId())))
				.confirmedRequests(confirmets)
				.views(getViews(event.getId()))
				.build();

		return EventMapper.toEventFullDto(event, eventData);
	}

	@Override
	public EventFullDto patchEvent(Long userId, Long eventId, @NonNull UpdateEventUserRequest request) {
		if (request.participantLimit() != null && request.participantLimit() < 0) {
			throw new ValidationException(
					"Ограничение на количество участников должно быть положительным числом");
		}

		userFeignRepository.checkUser(userId);
		return patchEvent(eventId, request, false);
	}

	@Override
	public long getInitiatorIfPublished(Long eventId) {
		checkEvent(eventId);
		long initiatorId = eventRepository.getInitiatorIdByEventId(eventId);
		userFeignRepository.checkUser(initiatorId);
		return initiatorId;
	}

	private void checkEvent(Long eventId) {
		if (!eventRepository.existsByIdAndState(eventId, EventState.PUBLISHED)) {
			throw new NotFoundException(
					"Событие с id=%s не существует или не опубликовано.".formatted(eventId));
		}
	}

	@SuppressWarnings("SameParameterValue")
	private EventFullDto patchEvent(Long eventId,
	                                @NonNull UpdateEventUserRequest request,
	                                boolean isAdmin) {
		try {
			Event event = getEventById(eventId);

			if (!isAdmin && event.getState() == EventState.PUBLISHED) {
				throw new ConflictException("Нельзя редактировать опубликованное событие");
			}

			if (request.eventDate() != null) {
				LocalDateTime eventDateTime = request.eventDate();
				LocalDateTime minDateTime = LocalDateTime.now().plusHours(HOURS_BEFORE_START);

				if (!isAdmin && eventDateTime.isBefore(minDateTime)) {
					throw new ValidationException(
							String.format(
									"Дата события должна быть не ранее чем за %d часа(ов) до начала",
									HOURS_BEFORE_START
							)
					);
				}
			}

			UserStateAction action = request.stateAction();

			if (action != null) {
				EventState newState = isAdmin
						? StateMapper.mapAdminEventAction(action)
						: StateMapper.mapUserEventAction(action);

				if (EventState.PUBLISHED.equals(newState)) {
					event.setPublishedOn(LocalDateTime.from(Instant.now()));
				}
				if (newState != null) {
					event.setState(newState);
				}
			}

			if (request.category() != null) {
				Category category = getCategoryById(request.category());
				event.setCategory(category);
			}

			EventMapper.merge(event, request);

			Event patched = eventRepository.save(event);

			log.info("Событие обновлено: {}", patched.getId());

			return EventMapper.toEventFullDto(event, getEventData(eventId, event.getInitiatorId()));

		} catch (DataIntegrityViolationException e) {
			log.debug("Конфликт во время обновления события {}", request, e);
			throw new ConflictException("Конфликт с другим событием");
		}
	}

	private List<EventRequestCount> fetchConfirmedRequestsCount(List<Long> events) {
		return requestFeignRepository.getConfirmedRequestsCount(events);
	}

	@NonNull
	private List<String> getUris(@NonNull List<Event> events) {
		return events.stream().map(event -> EVENTS_PATH + event.getId()).toList();
	}

	private Map<Long, Long> getRequestCountMap(@NonNull List<EventRequestCount> eventRequestCountList) {
		return eventRequestCountList.stream()
				.collect(Collectors.toMap(EventRequestCount::eventId, EventRequestCount::count,
						(a, b) -> a));
	}

	@NonNull
	private Map<Long, UserShortDto> getUserShortDtoMap(@NonNull List<Event> events) {
		List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
		List<UserShortDto> users = userFeignRepository.getUsersByIds(userIds);
		return users.stream()
				.collect(Collectors.toMap(UserShortDto::id, user -> user));
	}

	private EventData getEventData(long eventId, long initiatorId) {
		return EventData.builder()
				.initiator(UserMapper.toUserShortDto(getUserById(initiatorId)))
				.confirmedRequests(getConfirmedRequestsCountByEventId(eventId))
				.views(getViews(eventId))
				.build();
	}

	@NonNull
	private UserDto getUserById(long userId) {
		return userFeignRepository.getUserDtoById(userId);
	}

	@NonNull
	private Event getEventById(long eventId) {
		return eventRepository.findById(eventId).orElseThrow(
				() -> new NotFoundException("Событие с id=%s не найдено".formatted(eventId))
		);
	}

	@NonNull
	private Category getCategoryById(long categoryId) {
		return categoryRepository.findById(categoryId).orElseThrow(
				() -> new NotFoundException("Категория с id=%s не найдена".formatted(categoryId))
		);
	}

	private long getConfirmedRequestsCountByEventId(long eventId) {
		var counts = fetchConfirmedRequestsCount(List.of(eventId));
		if (counts.isEmpty()) {
			return 0;
		}

		return counts.getFirst().count();
	}

	private long getViews(long eventId) {
		var uris = List.of(EVENTS_PATH + eventId);
		var statsOptional = statsFeignRepository.getStatList(
				StatsRequestData.builder()
						.uris(uris)
						.unique(true)
						.build()
		);

		if (statsOptional.isEmpty()) {
			return 1;
		}

		List<ViewStatsDto> stats = statsOptional.get();

		if (stats.isEmpty()) {
			return 0;
		}
		return stats.getFirst().getHits();
	}

	private long getConfirmedRequestsCountFromMap(@NonNull
	                                              Map<Long, Long> requestCountMap,
	                                              long eventId) {
		if (requestCountMap.isEmpty()) {
			return 0;
		}
		if (requestCountMap.containsKey(eventId)) {
			return requestCountMap.get(eventId);
		}
		return 0;
	}

	private long getViewsFromStatsList(@NonNull List<ViewStatsDto> viewStatsMap, long eventId) {
		return viewStatsMap.stream()
				.filter(statsDto -> statsDto.getUri().equals(EVENTS_PATH + eventId))
				.map(ViewStatsDto::getHits)
				.findAny()
				.orElse(0L);
	}

}

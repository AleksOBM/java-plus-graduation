package ru.practicum.ewm.events.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.event.come.create.NewEventDto;
import ru.practicum.aggregation.dto.event.come.update.UpdateEventUserRequest;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.event.output.EventShortDto;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.ewm.events.service.event.EventService;
import ru.practicum.ewm.events.service.request.EventRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventController {

	private final EventService eventService;
	private final EventRequestService eventRequestService;

	/**
	 * @param from   количество элементов, которые нужно пропустить для формирования текущего набора</br>
	 *               Default value : 0
	 * @param size   количество элементов в наборе</br>
	 *               Default value : 10
	 */
	@GetMapping
	public List<EventShortDto> findEventsByUserId(
			@PathVariable @Positive Long userId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size,
			@NonNull HttpServletRequest request
	) {
		log.info("""
				ENDPOINT
				Получение событий, добавленных текущим пользователем
				{} {}""", request.getMethod(), request.getRequestURI());

		return eventService.findByUserId(userId, from, size);
	}

	@GetMapping("/{eventId}")
	public EventFullDto findEventById(@PathVariable @Positive Long userId,
	                                  @PathVariable @Positive Long eventId,
	                                  @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Получение полной информации о событии добавленном текущим пользователем
				{} {}""", request.getMethod(), request.getRequestURI());
		return eventService.findEventByUserIdAndEventId(userId, eventId);
	}

	/**
	 * Обратите внимание: дата и время на которые намечено событие не может быть раньше,
	 * чем через два часа от текущего момента
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EventFullDto addEvent(@PathVariable @Positive Long userId,
	                             @RequestBody @Valid NewEventDto newEventDto,
	                             @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Добавление нового события
				{} {}""", request.getMethod(), request.getRequestURI());
		return eventService.userAddNewEvent(userId, newEventDto);
	}

	/**
	 * Обратите внимание:
	 * <p>
	 * - изменить можно только отмененные события или события в состоянии ожидания модерации</br>
	 * (Ожидается код ошибки 409)
	 * <p>
	 * - дата и время на которые намечено событие не может быть раньше,
	 * чем через два часа от текущего момента</br>
	 * (Ожидается код ошибки 409)
	 */
	@PatchMapping("/{eventId}")
	public EventFullDto patchEvent(@PathVariable @Positive Long userId,
	                               @PathVariable @Positive Long eventId,
	                               @RequestBody @Valid UpdateEventUserRequest updateRequest,
	                               @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Изменение события добавленного текущим пользователем
				{} {}""", request.getMethod(), request.getRequestURI());
		return eventService.patchEvent(userId, eventId, updateRequest);
	}

	/**
	 * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
	 */
	@GetMapping("/{eventId}/requests")
	public List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId,
	                                                 @PathVariable @Positive Long eventId,
	                                                 @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Получение информации о запросах на участие в событии текущего пользователя
				{} {}""", request.getMethod(), request.getRequestURI());
		return eventRequestService.findByEventId(userId, eventId);
	}

	/**
	 * Обратите внимание:
	 * <p>
	 * - если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
	 * <p>
	 * - нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
	 * <p>
	 * - статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
	 * <p>
	 * - если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки
	 * необходимо отклонить
	 */
	@PatchMapping("/{eventId}/requests")
	public EventRequestStatusUpdateResult patchRequests(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long eventId,
			@RequestBody @NotNull @Valid EventRequestStatusUpdateRequest updateRequest,
			@NonNull HttpServletRequest request
	) {
		log.info("""
				ENDPOINT
				Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
				{} {}""", request.getMethod(), request.getRequestURI());
		return eventRequestService.updateStatusRequest(userId, eventId, updateRequest);
	}

}

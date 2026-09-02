package ru.practicum.ewm.events.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.model.data.AdminGetData;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.event.come.update.UpdateEventAdminRequest;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.ewm.events.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

	private final EventService eventService;

	/**
	 * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
	 * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
	 */
	@GetMapping
	public List<EventFullDto> adminGetEvents(
			@RequestParam(required = false)
			List<Integer> users,

			@RequestParam(required = false)
			List<EventState> states,

			@RequestParam(required = false)
			List<Integer> categories,

			@RequestParam(required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			LocalDateTime rangeStart,

			@RequestParam(required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			LocalDateTime rangeEnd,

			@RequestParam(required = false, defaultValue = "0")
			Integer from,

			@RequestParam(required = false, defaultValue = "10")
			Integer size,

			@NonNull HttpServletRequest request
	) {

		log.info("""
				ENDPOINT
				Поиск событий
				{} {}""", request.getMethod(), request.getRequestURI());

		AdminGetData getDto = AdminGetData.builder()
				.users(users)
				.states(states)
				.categories(categories)
				.rangeStart(rangeStart)
				.rangeEnd(rangeEnd)
				.from(from)
				.size(size)
				.build();

		return eventService.adminGetEvents(getDto);
	}

	/**
	 * Редактирование данных любого события администратором.</br>
	 * Валидация данных не требуется.</br>
	 * Обратите внимание:</br>
	 * - дата начала изменяемого события должна быть не ранее чем за час от даты публикации. </br>
	 * (Ожидается код ошибки 409)</br>
	 * - событие можно публиковать, только если оно в состоянии ожидания публикации </br>
	 * (Ожидается код ошибки 409)</br>
	 * - событие можно отклонить, только если оно еще не опубликовано </br>
	 * (Ожидается код ошибки 409)
	 */
	@PatchMapping("/{eventId}")
	public EventFullDto adminUpdateEvent(
			@PathVariable Long eventId,
			@RequestBody @Valid UpdateEventAdminRequest updateRequest,
			@NonNull HttpServletRequest request) {

		log.info("""
				ENDPOINT
				Редактирование данных события и его статуса (отклонение/публикация)
				{} {}""", request.getMethod(), request.getRequestURI());

		return eventService.adminUpdateEvent(eventId, updateRequest);
	}

}

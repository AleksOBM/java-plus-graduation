package ru.practicum.ewm.events.controller.free;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.event.output.EventShortDto;
import ru.practicum.aggregation.model.data.FreeGetData;
import ru.practicum.ewm.events.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FreeEventController {

	EventService eventService;

	/**
	 * Обратите внимание:<br/>
	 * - это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события<br/>
	 * - текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв<br/>
	 * - если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события,
	 * которые произойдут позже текущей даты и времени<br/>
	 * - информация о каждом событии должна включать в себя количество просмотров и количество
	 * уже одобренных заявок на участие<br/>
	 * - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно
	 * сохранить в сервисе статистики<br/>
	 * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
	 */
	@GetMapping
	public List<EventShortDto> getFreeEvents(

			@RequestParam(required = false)
			String text,

			@RequestParam(required = false)
			List<Integer> categories,

			@RequestParam(required = false)
			Boolean paid,

			@RequestParam(required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			LocalDateTime rangeStart,

			@RequestParam(required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			LocalDateTime rangeEnd,

			@RequestParam(required = false, defaultValue = "false")
			Boolean onlyAvailable,

			@RequestParam(required = false)
			FreeGetData.FreeEventSort sort,

			@RequestParam(required = false, defaultValue = "0")
			Integer from,

			@RequestParam(required = false, defaultValue = "10")
			Integer size,

			@NonNull HttpServletRequest request
	) {
		FreeGetData freeGetData = FreeGetData.builder()
				.text(text)
				.categories(categories)
				.paid(paid)
				.rangeStart(rangeStart)
				.rangeEnd(rangeEnd)
				.onlyAvailable(onlyAvailable)
				.sort(sort)
				.from(from)
				.size(size)
				.build();

		log.info("""
				ENDPOINT
				Получение событий с возможностью фильтрации
				{} {}""", request.getMethod(), request.getRequestURI());

		return eventService.getFreeEvents(freeGetData, request);
	}

	@GetMapping(value = "/{eventId}")
	public EventFullDto getFreeEventById(@PathVariable Long eventId,
	                                     @NonNull HttpServletRequest request) {

		log.info("""
				ENDPOINT
				Получение подробной информации об опубликованном событии по его идентификатору.
				{} {}""", request.getMethod(), request.getRequestURI());

		return eventService.getFreeEventById(eventId, request);
	}

}

package ru.practicum.ewm.events.service.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.event.come.create.NewEventDto;
import ru.practicum.aggregation.dto.event.come.update.UpdateEventAdminRequest;
import ru.practicum.aggregation.dto.event.come.update.UpdateEventUserRequest;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.event.output.EventShortDto;
import ru.practicum.aggregation.model.data.AdminGetData;
import ru.practicum.aggregation.model.data.FreeGetData;

import java.util.List;

@Transactional(readOnly = true)
public interface EventService {

	/// Получение событий незарегистрированным пользователем с возможностью фильтрации
	List<EventShortDto> getFreeEvents(FreeGetData freeGetData, HttpServletRequest request);

	/**
	 * Получение незарегистрированным пользователем подробной информации об опубликованном событии
	 * по его идентификатору
	 */
	EventFullDto getFreeEventById(Long eventId, HttpServletRequest request);

	/// Добавление нового события зарегистрированным пользователем
	@Transactional
	EventFullDto userAddNewEvent(Long userId, NewEventDto newEventDto);

	/// Поиск событий администратором
	List<EventFullDto> adminGetEvents(AdminGetData adminGetData);

	/// Редактирование администратором данных события и его статуса (отклонение/публикация)
	@Transactional
	EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest request);

	/// Получение событий, добавленных текущим пользователем
	List<EventShortDto> findByUserId(Long userId, Integer from, Integer size);

	/// Получение полной информации о событии добавленном текущим пользователем
	EventFullDto findEventByUserIdAndEventId(Long userId, Long eventId);

	/// Получение полной информации о событии по ID
	EventFullDto findEventById(long eventId, long confirmets);

	/// Изменение события добавленного текущим пользователем
	@Transactional
	EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest request);

	long getInitiatorIfPublished(@Positive Long eventId);
}

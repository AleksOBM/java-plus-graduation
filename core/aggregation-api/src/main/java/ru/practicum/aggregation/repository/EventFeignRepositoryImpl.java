package ru.practicum.aggregation.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteCallExecutor;
import ru.practicum.aggregation.client.RemoteCallResult;
import ru.practicum.aggregation.client.event.EventClient;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;
import ru.practicum.aggregation.error.exception.unavailable.UserServiceUnavailableException;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventFeignRepositoryImpl implements EventFeignRepository {

	EventClient eventClient;

	@SuppressWarnings("unused")
	@Override
	public void updateRating(@NonNull RatingUpdateRequest request) {
		log.info("""
				PLEASE WAITING
				Система обновляет рейтинг
				request: {}""", request);
		var eventId = request.eventId();
		switch (RemoteCallExecutor.executeVoid(() -> eventClient.systemUpdateRating(request))) {
			case RemoteCallResult.Success(var nullable) -> {
			}
			case RemoteCallResult.Failure(var exeption) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось обновить рейтинг события с id={}""", eventId);
				throw exeption;
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось обновить рейтинг события с id={}""", eventId);
				throw new UserServiceUnavailableException(cause);
			}
		}
	}

	@Override
	public EventFullDto userFindEventById(long userId, long eventId) {
		log.info("""
				PLEASE WAITING
				Пользователь с id={} ищет событие с id={}""", userId, eventId);
		return switch (RemoteCallExecutor.execute(() -> eventClient.userFindEventById(userId, eventId))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var exeption) -> {
				log.warn("""
						Бизнес-исключение
						Пользователю с id={}
						Не удалось получить событие с id={}""", userId, eventId);
				throw exeption;
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Пользователю с id={}
						Не удалось получить событие с id={}""", userId, eventId);
				throw new UserServiceUnavailableException(cause);
			}
		};
	}

	@Override
	public EventFullDto systemFindEventById(long eventId) {
		log.info("""
				PLEASE WAITING
				Система ищет событие с id={}""", eventId);
		return switch (RemoteCallExecutor.execute(() -> eventClient.systemFindEventById(eventId))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var exeption) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить событие с id={}""", eventId);
				throw exeption;
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить событие с id={}""", eventId);
				throw new UserServiceUnavailableException(cause);
			}
		};
	}

}

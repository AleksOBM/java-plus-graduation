package ru.practicum.aggregation.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteCallExecutor;
import ru.practicum.aggregation.client.RemoteCallResult;
import ru.practicum.aggregation.client.request.RequestClient;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.error.exception.unavailable.RequestServiceUnavailableException;
import ru.practicum.aggregation.model.repository.EventRequestCount;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestFeignRepositoryImpl implements RequestFeignRepository {

	RequestClient requestClient;

	@Override
	public List<EventRequestCount> getConfirmedRequestsCount(List<Long> eventIds) {
		log.info("""
				PLEASE WAITING
				Система получает количество подтвержденных запросов на участие в событиях
				eventIds: {}""", eventIds);
		return switch (RemoteCallExecutor.execute(() ->
				requestClient.getConfirmedRequestsCount(eventIds))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var exeption) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить количество подтвержденных заявок на участие в событиях
						eventIds: {}""", eventIds);
				throw exeption;
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить количество подтвержденных заявок на участие в событиях
						eventIds: {}""", eventIds);
				throw new RequestServiceUnavailableException(cause);
			}
		};
	}

	@Override
	public List<ParticipationRequestDto> findByUserIdAndEventId(long userId, long eventId) {
		log.info("""
				PLEASE WAITING
				Система ищет запрос на участие пользователя с id={} в событии с id={}""", userId, eventId);
		return switch (RemoteCallExecutor.execute(() -> requestClient
				.findByUserIdAndEventId(userId, eventId))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var exeption) -> {
				log.warn("""
						Бизнес-исключение
						Пользователю с id={}
						Не удалось получить запросы на участие в событии с id={}""", userId, eventId);
				throw exeption;
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Пользователю с id={}
						Не удалось получить запросы на участие в событии с id={}""", userId, eventId);
				throw new RequestServiceUnavailableException(cause);
			}
		};
	}

	@Override
	public EventRequestStatusUpdateResult updateStatusRequest(Long eventId,
	                                                          Integer participantLimit,
	                                                          Boolean requestModeration,
	                                                          EventRequestStatusUpdateRequest request) {
		log.info("""
				PLEASE WAITING
				Система обновляет статус запросов на участие в событии с id={}
				{}""", eventId, request);
		return switch (RemoteCallExecutor.execute(() ->
				requestClient.updateStatusRequest(eventId, participantLimit, requestModeration, request))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var exeption) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось обновить статус запросов на участие в событии с id={}
						request: {}""", eventId, request);
				throw exeption;
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось обновить статус запросов на участие в событии с id={}
						{}""", eventId, request);
				throw new RequestServiceUnavailableException(cause);
			}
		};
	}

}

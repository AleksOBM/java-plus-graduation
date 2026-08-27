package ru.practicum.aggregation.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteCallExecutor;
import ru.practicum.aggregation.client.RemoteCallResult;
import ru.practicum.aggregation.client.request.RequestClient;
import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.enums.ParticipationStatus;
import ru.practicum.aggregation.error.exception.processing.RequestProcessingException;
import ru.practicum.aggregation.error.exception.unavailable.RequestServiceUnavailableException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestFeignRepositoryImpl implements RequestFeignRepository {

	RequestClient requestClient;

	@Override
	public List<EventRequestCount> getConfirmedRequestsCount(List<Long> eventIds) {
		return switch (RemoteCallExecutor.execute(() ->
				requestClient.getCount(eventIds, ParticipationStatus.CONFIRMED))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var message) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить количество подтвержденных заявок на участие в событиях
						eventIds: {}
						""", eventIds);
				throw new RequestProcessingException(message);
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить количество подтвержденных заявок на участие в событиях
						eventIds: {}
						""", eventIds);
				throw new RequestServiceUnavailableException(cause);
			}
		};
	}

	@Override
	public List<ParticipationRequestDto> findByEventId(long userId, long eventId) {
		return switch (RemoteCallExecutor.execute(() -> requestClient.findByEventId(userId, eventId))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var message) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить запросы пользователей на участие в событии с id={}
						""", eventId);
				throw new RequestProcessingException(message);
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить запросы пользователей на участие в событии с id={}
						""", eventId);
				throw new RequestServiceUnavailableException(cause);
			}
		};
	}

	@Override
	public EventRequestStatusUpdateResult updateStatusRequest(Long userId,
	                                                          Long eventId,
	                                                          EventRequestStatusUpdateRequest status) {
		return switch (RemoteCallExecutor.execute(() ->
				requestClient.updateStatusRequest(userId, eventId, status))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var message) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить запросы пользователей на участие
						в событии с id={}, для пользователя с id={}
						""", eventId, userId);
				throw new RequestProcessingException(message);
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить запросы пользователей на участие
						в событии с id={}, для пользователя с id={}
						""", eventId, userId);
				throw new RequestServiceUnavailableException(cause);
			}
		};
	}

}

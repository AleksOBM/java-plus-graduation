package ru.practicum.aggregation.client.request;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteExceptionMapper;
import ru.practicum.aggregation.dto.request.EventRequestCount;
import ru.practicum.aggregation.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.request.ParticipationRequestDto;
import ru.practicum.aggregation.enums.ParticipationStatus;

import java.util.List;

@Component
public class RequestFallbackFactory implements FallbackFactory<RequestClient> {

	@Override
	public RequestClient create(Throwable cause) {

		return new RequestClient() {

			@Override
			public List<ParticipationRequestDto> findByEventId(Long userId, Long eventId) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}

			@Override
			public EventRequestStatusUpdateResult updateStatusRequest(Long userId,
			                                                          Long eventId,
			                                                          EventRequestStatusUpdateRequest request) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}

			@Override
			public List<EventRequestCount> getCount(List<Long> eventIds, ParticipationStatus status) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}
		};
	}

}

package ru.practicum.aggregation.client.request;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteExceptionMapper;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.model.repository.EventRequestCount;

import java.util.List;

@Component
public class RequestFallbackFactory implements FallbackFactory<RequestClient> {

	@Override
	public RequestClient create(Throwable cause) {

		return new RequestClient() {

			@Override
			public List<ParticipationRequestDto> findByUserIdAndEventId(Long userId, Long eventId) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}

			@Override
			public EventRequestStatusUpdateResult updateStatusRequest(Long eventId,
			                                                          EventRequestStatusUpdateRequest request) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}

			@Override
			public List<EventRequestCount> getConfirmedRequestsCount(List<Long> eventIds) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}
		};
	}

}

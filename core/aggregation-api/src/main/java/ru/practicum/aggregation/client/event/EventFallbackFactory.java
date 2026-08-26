package ru.practicum.aggregation.client.event;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteExceptionMapper;
import ru.practicum.aggregation.dto.event.response.EventFullDto;
import ru.practicum.aggregation.dto.rating.RatingUpdateRequest;

@Component
public class EventFallbackFactory implements FallbackFactory<EventClient> {

	@Override
	public EventClient create(Throwable cause) {

		return new EventClient() {

			@Override
			public EventFullDto userFindEventById(Long userId, Long eventId) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}

			@Override
			public void systemUpdateRating(RatingUpdateRequest request) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}

			@Override
			public EventFullDto systemFindEventById(Long eventId) {
				throw RemoteExceptionMapper.mapRequestException(cause);
			}
		};
	}

}

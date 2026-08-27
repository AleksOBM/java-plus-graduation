package ru.practicum.aggregation.client.event;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteExceptionMapper;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;

@Component
public class EventFallbackFactory implements FallbackFactory<EventClient> {

	@Override
	public EventClient create(Throwable cause) {

		return new EventClient() {

			@Override
			public EventFullDto userFindEventById(Long userId, Long eventId) {
				throw RemoteExceptionMapper.mapEventException(cause);
			}

			@Override
			public void systemUpdateRating(RatingUpdateRequest request) {
				throw RemoteExceptionMapper.mapEventException(cause);
			}

			@Override
			public EventFullDto systemFindEventById(Long eventId) {
				throw RemoteExceptionMapper.mapEventException(cause);
			}
		};
	}

}

package ru.practicum.ewm.events.service.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.repository.RequestFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepositoryImpl;
import ru.practicum.ewm.events.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventRequestServiceImpl implements EventRequestService {

	EventRepository eventRepository;

	RequestFeignRepository requestFeignRepository;
	UserFeignRepositoryImpl userFeignRepository;

	@Override
	public List<ParticipationRequestDto> findByEventId(long userId, long eventId) {
		checkEvent(eventId);
		userFeignRepository.checkUser(userId);

		return requestFeignRepository.findByUserIdAndEventId(userId, eventId);
	}

	@Override
	public EventRequestStatusUpdateResult updateStatusRequest(long userId,
	                                                          long eventId,
	                                                          EventRequestStatusUpdateRequest request
	) {
		userFeignRepository.checkUser(userId);
		var event = eventRepository.findById(eventId).orElseThrow(
				() -> new NotFoundException("Событие с id=%s не найдено".formatted(eventId))
		);

		if (!event.getInitiatorId().equals(userId)) {
			throw new ConflictException("Пользователь с id=%s не является инициатором события с id=%s"
					.formatted(eventId, userId)
			);
		}

		return requestFeignRepository.updateStatusRequest(
				eventId, event.getParticipantLimit(), event.isRequestModeration(), request);
	}

	private void checkEvent(long eventId) {
		if (!eventRepository.existsById(eventId)) {
			throw new NotFoundException("Событие с id=%s не найдено".formatted(eventId));
		}
	}

}

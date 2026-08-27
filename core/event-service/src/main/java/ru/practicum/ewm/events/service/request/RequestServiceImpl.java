package ru.practicum.ewm.events.service.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.error.exception.NotFoundException;
import ru.practicum.aggregation.repository.RequestFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepositoryImpl;
import ru.practicum.ewm.events.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {

	EventRepository eventRepository;

	RequestFeignRepository requestFeignRepository;
	UserFeignRepositoryImpl userFeignRepository;

	@Override
	public List<ParticipationRequestDto> findByEventId(Long userId, Long eventId) {
		checkEvent(eventId);
		userFeignRepository.checkUser(userId);

		return requestFeignRepository.findByEventId(userId, eventId);
	}

	@Override
	public EventRequestStatusUpdateResult updateStatusRequest(Long userId,
	                                                          Long eventId,
	                                                          EventRequestStatusUpdateRequest status
	) {
		checkEvent(eventId);
		userFeignRepository.checkUser(userId);
		return requestFeignRepository.updateStatusRequest(userId, eventId, status);
	}

	private void checkEvent(Long eventId) {
		if (!eventRepository.existsById(eventId)) {
			throw new NotFoundException("Событие с id=%s не найдено".formatted(eventId));
		}
	}

}

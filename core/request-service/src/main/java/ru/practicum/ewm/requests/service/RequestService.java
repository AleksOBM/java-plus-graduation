package ru.practicum.ewm.requests.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.model.repository.EventRequestCount;

import java.util.List;

@Transactional
public interface RequestService {

	@Transactional(readOnly = true)
	List<ParticipationRequestDto> findByUserIdAndEventId(long userId, long eventId);

	@Transactional(readOnly = true)
	List<ParticipationRequestDto> findByRequesterId(long userId);

	@Transactional(readOnly = true)
	List<EventRequestCount> getRequestsCount(List<Long> eventIds);

	EventRequestStatusUpdateResult updateStatusRequest(long eventId,
	                                                   int participantLimit,
	                                                   boolean requestModeration,
	                                                   EventRequestStatusUpdateRequest request);

	ParticipationRequestDto addParticipationRequest(long userId, long eventId);

	ParticipationRequestDto cancelParticipationRequest(long userId, long requestId);
}

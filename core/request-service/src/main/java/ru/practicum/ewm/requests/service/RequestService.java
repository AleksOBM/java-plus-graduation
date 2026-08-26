package ru.practicum.ewm.requests.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.request.EventRequestCount;
import ru.practicum.aggregation.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.request.ParticipationRequestDto;
import ru.practicum.aggregation.enums.ParticipationStatus;

import java.util.List;

@Transactional
public interface RequestService {

	@Transactional(readOnly = true)
	List<ParticipationRequestDto> findByEventId(Long userId, Long eventId);

	@Transactional(readOnly = true)
	List<ParticipationRequestDto> findByRequesterId(Long userId);

	@Transactional(readOnly = true)
	List<EventRequestCount> getRequestsCount(List<Long> eventIds, ParticipationStatus status);

	EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
	                                                   EventRequestStatusUpdateRequest request);

	ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

	ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);
}

package ru.practicum.ewm.events.service.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;

import java.util.List;

@Transactional
public interface EventRequestService {

	@Transactional(readOnly = true)
	List<ParticipationRequestDto> findByEventId(long userId, long eventId);

	EventRequestStatusUpdateResult updateStatusRequest(long userId,
	                                                   long eventId,
	                                                   EventRequestStatusUpdateRequest status);
}

package ru.practicum.ewm.events.service.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;

import java.util.List;

@Transactional
public interface RequestService {

	@Transactional(readOnly = true)
	List<ParticipationRequestDto> findByEventId(Long userId, Long eventId);

	EventRequestStatusUpdateResult updateStatusRequest(Long userId,
	                                                   Long eventId,
	                                                   EventRequestStatusUpdateRequest status);
}

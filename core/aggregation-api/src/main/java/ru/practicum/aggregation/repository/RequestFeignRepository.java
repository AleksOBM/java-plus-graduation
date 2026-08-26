package ru.practicum.aggregation.repository;

import ru.practicum.aggregation.dto.request.EventRequestCount;
import ru.practicum.aggregation.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestFeignRepository {

	List<EventRequestCount> getConfirmedRequestsCount(List<Long> eventIds);

	List<ParticipationRequestDto> findByEventId(long userId, long eventId);

	EventRequestStatusUpdateResult updateStatusRequest(Long userId,
	                                                   Long eventId,
	                                                   EventRequestStatusUpdateRequest status);
}

package ru.practicum.aggregation.repository;

import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;

import java.util.List;

public interface RequestFeignRepository {

	List<EventRequestCount> getConfirmedRequestsCount(List<Long> eventIds);

	List<ParticipationRequestDto> findByEventId(long userId, long eventId);

	EventRequestStatusUpdateResult updateStatusRequest(Long userId,
	                                                   Long eventId,
	                                                   EventRequestStatusUpdateRequest status);
}

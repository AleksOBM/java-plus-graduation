package ru.practicum.aggregation.repository;

import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;

public interface EventFeignRepository {

	void updateRating(RatingUpdateRequest request);

	EventFullDto systemFindEventById(long eventId, long confirmets);

	Long getInitiatorIfPublished(Long eventId);
}

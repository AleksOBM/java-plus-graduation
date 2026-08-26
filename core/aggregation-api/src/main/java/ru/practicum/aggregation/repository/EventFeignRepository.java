package ru.practicum.aggregation.repository;

import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.event.response.EventFullDto;
import ru.practicum.aggregation.dto.rating.RatingUpdateRequest;

public interface EventFeignRepository {

	void updateRating(@NonNull RatingUpdateRequest request);

	EventFullDto userFindEventById(long userId, long eventId);

	EventFullDto systemFindEventById(long eventId);
}

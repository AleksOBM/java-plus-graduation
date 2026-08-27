package ru.practicum.aggregation.repository;

import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;

public interface EventFeignRepository {

	void updateRating(@NonNull RatingUpdateRequest request);

	EventFullDto userFindEventById(long userId, long eventId);

	EventFullDto systemFindEventById(long eventId);
}

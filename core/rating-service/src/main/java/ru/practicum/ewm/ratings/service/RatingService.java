package ru.practicum.ewm.ratings.service;

import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.rating.RatingRequest;
import ru.practicum.aggregation.dto.rating.RatingResponse;

@Transactional
public interface RatingService {

	RatingResponse addOrUpdateReaction(long userId, long eventId, @NonNull RatingRequest request);

	void removeReaction(long userId, long eventId);
}

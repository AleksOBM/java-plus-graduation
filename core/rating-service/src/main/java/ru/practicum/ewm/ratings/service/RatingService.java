package ru.practicum.ewm.ratings.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.rating.come.create.RatingCreateRequest;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;

@Transactional
public interface RatingService {

	RatingResponse addOrUpdateReaction(long userId, long eventId, RatingCreateRequest request);

	void removeReaction(long userId, long eventId);
}

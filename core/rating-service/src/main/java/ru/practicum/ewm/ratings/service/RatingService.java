package ru.practicum.ewm.ratings.service;

import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.rating.come.create.RatingCreateRequest;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;

@Transactional
public interface RatingService {

	RatingResponse addOrUpdateReaction(long userId, long eventId, @NonNull RatingCreateRequest request);

	void removeReaction(long userId, long eventId);
}

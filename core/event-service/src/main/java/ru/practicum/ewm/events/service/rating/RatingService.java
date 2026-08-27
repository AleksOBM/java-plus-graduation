package ru.practicum.ewm.events.service.rating;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;

@Transactional
public interface RatingService {

	void updateRating(RatingUpdateRequest request);
}

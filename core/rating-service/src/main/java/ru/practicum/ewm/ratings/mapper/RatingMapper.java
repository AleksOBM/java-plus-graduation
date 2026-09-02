package ru.practicum.ewm.ratings.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;
import ru.practicum.ewm.ratings.entity.Rating;

@UtilityClass
public class RatingMapper {

	public RatingResponse mapToResponse(@NonNull Rating rating) {
		return RatingResponse.builder()
				.id(rating.getId())
				.userId(rating.getUserId())
				.eventId(rating.getEventId())
				.reaction(rating.getReaction())
				.build();
	}
}

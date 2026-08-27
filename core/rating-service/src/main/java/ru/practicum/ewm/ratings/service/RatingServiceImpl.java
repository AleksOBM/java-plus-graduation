package ru.practicum.ewm.ratings.service;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.rating.come.create.RatingCreateRequest;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;
import ru.practicum.aggregation.enums.Reaction;
import ru.practicum.aggregation.error.exception.ConflictException;
import ru.practicum.aggregation.error.exception.NotFoundException;
import ru.practicum.aggregation.repository.EventFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepository;
import ru.practicum.ewm.ratings.entity.Rating;
import ru.practicum.ewm.ratings.repository.RatingRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {

	RatingRepository ratingRepository;

	UserFeignRepository userFeignRepository;
	EventFeignRepository eventFeignRepository;

	@Override
	public RatingResponse addOrUpdateReaction(long userId, long eventId, @NonNull RatingCreateRequest request) {
		var user = userFeignRepository.getUserDtoById(userId);
		var event = eventFeignRepository.userFindEventById(userId, eventId);
		var initiator = event.initiator();

		if (Objects.equals(user.id(), initiator.id())) {
			throw new ValidationException("Нельзя ставить реакции своим событиям");
		}

		var rating = ratingRepository.findByUserIdAndEventId(userId, eventId).orElse(null);
		var requestReaction = request.reaction();

		if (rating != null) {
			if (rating.getReaction() == requestReaction) {
				ratingRepository.delete(rating);
				updateEventRate(eventId);
				throw new ConflictException("Reaction removed");
			} else {
				rating.setReaction(requestReaction);
				ratingRepository.save(rating);
				updateEventRate(eventId);
				return mapToResponse(rating);
			}
		} else {
			rating = Rating.builder()
					.userId(userId)
					.eventId(eventId)
					.reaction(requestReaction)
					.build();
			ratingRepository.save(rating);
			updateEventRate(eventId);
			return mapToResponse(rating);
		}
	}

	@Override
	public void removeReaction(long userId, long eventId) {
		Rating rating = ratingRepository.findByUserIdAndEventId(userId, eventId)
				.orElseThrow(() -> new NotFoundException("Реакция не найдена"));
		ratingRepository.delete(rating);
		updateEventRate(eventId);
	}

	private void updateEventRate(long eventId) {
		long likes = ratingRepository.countByEventIdAndReaction(eventId, Reaction.LIKE);
		long dislikes = ratingRepository.countByEventIdAndReaction(eventId, Reaction.DISLIKE);
		long rate = likes - dislikes;

		var request = RatingUpdateRequest.builder()
				.eventId(eventId)
				.rate(rate)
				.build();

		eventFeignRepository.updateRating(request);
	}

	private RatingResponse mapToResponse(@NonNull Rating rating) {
		return RatingResponse.builder()
				.id(rating.getId())
				.userId(rating.getUserId())
				.eventId(rating.getEventId())
				.reaction(rating.getReaction())
				.build();
	}
}

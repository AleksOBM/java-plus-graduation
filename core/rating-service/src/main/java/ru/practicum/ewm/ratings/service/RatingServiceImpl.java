package ru.practicum.ewm.ratings.service;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.rating.come.create.RatingCreateRequest;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;
import ru.practicum.aggregation.enums.Reaction;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.repository.EventFeignRepository;
import ru.practicum.ewm.ratings.entity.Rating;
import ru.practicum.ewm.ratings.mapper.RatingMapper;
import ru.practicum.ewm.ratings.repository.RatingRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {

	RatingRepository ratingRepository;

	EventFeignRepository eventFeignRepository;

	@Override
	public RatingResponse addOrUpdateReaction(long userId,
	                                          long eventId,
	                                          @NonNull RatingCreateRequest request) {

		var initiatorId = eventFeignRepository.getInitiatorIfPublished(eventId);

		if (Objects.equals(userId, initiatorId)) {
			throw new ValidationException("Нельзя ставить реакции своим событиям");
		}

		var rating = ratingRepository
				.findByUserIdAndEventId(userId, eventId)
				.orElse(null);
		var requestReaction = request.reaction();

		if (rating != null) {
			if (rating.getReaction() == requestReaction) {
				ratingRepository.delete(rating);
				updateEventRate(eventId);
				throw new ConflictException("""
						Реакция пользователя с id=%s событию с id=%s, удалена"""
						.formatted(userId, eventId));
			} else {
				rating.setReaction(requestReaction);
				ratingRepository.save(rating);
				updateEventRate(eventId);
				return RatingMapper.mapToResponse(rating);
			}
		} else {
			rating = Rating.builder()
					.userId(userId)
					.eventId(eventId)
					.reaction(requestReaction)
					.build();
			ratingRepository.save(rating);
			updateEventRate(eventId);
			return RatingMapper.mapToResponse(rating);
		}
	}

	@Override
	public void removeReaction(long userId, long eventId) {
		ratingRepository.findByUserIdAndEventId(userId, eventId)
				.ifPresentOrElse(ratingRepository::delete,
						() -> {
							updateEventRate(eventId);
							throw new NotFoundException(
									"Реакция пользователя с id=%s событию с id=%s, не найдена"
											.formatted(userId, eventId)
							);
						}
				);

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

}

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
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.aggregation.enums.Reaction;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.aggregation.repository.EventFeignRepository;
import ru.practicum.aggregation.repository.RequestFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepository;
import ru.practicum.ewm.ratings.entity.Rating;
import ru.practicum.ewm.ratings.repository.RatingRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {

	RatingRepository ratingRepository;

	UserFeignRepository userFeignRepository;
	EventFeignRepository eventFeignRepository;
	RequestFeignRepository requestFeignRepository;

	@Override
	public RatingResponse addOrUpdateReaction(long userId,
	                                          long eventId,
	                                          @NonNull RatingCreateRequest request) {

		var user = userFeignRepository.getUserDtoById(userId);
		var confirmedRequestsCount = requestFeignRepository.getConfirmedRequestsCount(List.of(eventId));
		long contirmets = confirmedRequestsCount.stream()
				.map(EventRequestCount::count).findAny()
				.orElse(0L);
		var event = eventFeignRepository.systemFindEventById(eventId, contirmets);
		var initiator = event.initiator();

		if (Objects.equals(user.id(), initiator.id())) {
			throw new ValidationException("Нельзя ставить реакции своим событиям");
		}

		if (!event.state().equals(EventState.PUBLISHED)) {
			throw new NotFoundException("Нельзя поставить реакцию не опубликованному событию");
		}

		var rating = ratingRepository.findByUserIdAndEventId(userId, eventId).orElse(null);
		var requestReaction = request.reaction();

		if (rating != null) {
			if (rating.getReaction() == requestReaction) {
				ratingRepository.delete(rating);
				updateEventRate(eventId);
				throw new ConflictException("""
						Реакция пользователя с id=%s событию с id=%s, удалена""".formatted(userId, eventId));
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
		Rating rating = ratingRepository.findByUserIdAndEventId(userId, eventId).orElseThrow(() ->
				new NotFoundException(
						"Реакция пользователя с id=%s событию с id=%s, не найдена".formatted(userId, eventId)
				)
		);
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

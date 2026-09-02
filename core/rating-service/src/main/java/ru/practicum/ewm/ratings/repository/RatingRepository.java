package ru.practicum.ewm.ratings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.aggregation.enums.Reaction;
import ru.practicum.ewm.ratings.entity.Rating;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

	Optional<Rating> findByUserIdAndEventId(Long userId, Long eventId);

	long countByEventIdAndReaction(Long eventId, Reaction reaction);
}

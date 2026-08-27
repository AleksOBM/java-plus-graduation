package ru.practicum.ewm.ratings.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.rating.come.create.RatingCreateRequest;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;
import ru.practicum.ewm.ratings.service.RatingService;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/likes")
@RequiredArgsConstructor
public class RatingController {

	private final RatingService ratingService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RatingResponse addReaction(@PathVariable Long userId,
	                                  @PathVariable Long eventId,
	                                  @Valid @RequestBody RatingCreateRequest request) {
		return ratingService.addOrUpdateReaction(userId, eventId, request);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeReaction(@PathVariable Long userId,
	                           @PathVariable Long eventId) {
		ratingService.removeReaction(userId, eventId);
	}

}
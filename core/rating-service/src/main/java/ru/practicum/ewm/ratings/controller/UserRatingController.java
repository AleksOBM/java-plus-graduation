package ru.practicum.ewm.ratings.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.rating.come.create.RatingCreateRequest;
import ru.practicum.aggregation.dto.rating.output.RatingResponse;
import ru.practicum.ewm.ratings.service.RatingService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/likes")
@RequiredArgsConstructor
public class UserRatingController {

	private final RatingService ratingService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RatingResponse addReaction(@PathVariable Long userId,
	                                  @PathVariable Long eventId,
	                                  @Valid @RequestBody RatingCreateRequest createRequest,
	                                  @NonNull HttpServletRequest request) {

		log.info("""
				ENDPOINT
				Добавление новой реакции
				{} {}""", request.getMethod(), request.getRequestURI());

		return ratingService.addOrUpdateReaction(userId, eventId, createRequest);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeReaction(@PathVariable Long userId,
	                           @PathVariable Long eventId,
	                           @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Удаление реакции
				{} {}""", request.getMethod(), request.getRequestURI());

		ratingService.removeReaction(userId, eventId);
	}

}
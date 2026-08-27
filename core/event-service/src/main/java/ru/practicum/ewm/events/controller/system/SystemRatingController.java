package ru.practicum.ewm.events.controller.system;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;
import ru.practicum.ewm.events.service.rating.RatingService;

@Slf4j
@RestController
@RequestMapping(path = "/system/events/ratings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemRatingController {

	RatingService ratingService;

	@PatchMapping
	public void updateRating(@RequestBody @NotNull RatingUpdateRequest request) {
		ratingService.updateRating(request);
	}
}

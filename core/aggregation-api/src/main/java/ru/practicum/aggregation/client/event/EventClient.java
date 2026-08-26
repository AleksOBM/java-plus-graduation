package ru.practicum.aggregation.client.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.aggregation.dto.event.response.EventFullDto;
import ru.practicum.aggregation.dto.rating.RatingUpdateRequest;

@FeignClient(
		name = "event-service",
		fallbackFactory = EventFallbackFactory.class
)
public interface EventClient {

	@GetMapping("/users/{userId}/events/{eventId}")
	EventFullDto userFindEventById(@PathVariable @Positive Long userId,
	                               @PathVariable @Positive Long eventId);


	@PatchMapping("/system/events/ratings")
	void systemUpdateRating(@RequestBody @NotNull @Valid RatingUpdateRequest request);

	@GetMapping("/system/events/{eventId}")
	EventFullDto systemFindEventById(@PathVariable @Positive Long eventId);

}
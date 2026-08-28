package ru.practicum.aggregation.client.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;

@FeignClient(
		name = "event-service",
		fallbackFactory = EventFallbackFactory.class
)
public interface EventClient {

	@GetMapping("/users/{userId}/events/{eventId}")
	EventFullDto userFindEventById(@PathVariable Long userId,
	                               @PathVariable Long eventId);

	@PatchMapping("/system/events/ratings")
	void systemUpdateRating(@RequestBody @NotNull @Valid RatingUpdateRequest request);

	@GetMapping("/system/events/{eventId}")
	EventFullDto systemFindEventById(@PathVariable Long eventId);

}
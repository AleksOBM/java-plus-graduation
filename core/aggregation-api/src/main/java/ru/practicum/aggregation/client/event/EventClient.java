package ru.practicum.aggregation.client.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.rating.come.update.RatingUpdateRequest;

@FeignClient(
		name = "event-service",
		fallbackFactory = EventFallbackFactory.class
)
public interface EventClient {

	@PatchMapping("/system/events/ratings")
	void systemUpdateRating(@RequestBody @NotNull @Valid RatingUpdateRequest request);

	@GetMapping("/system/events/{eventId}")
	EventFullDto systemFindEventById(@PathVariable Long eventId, @RequestParam Long confirmets);

	@GetMapping("/system/events/{eventId}/initiator")
	Long systemGetInitiatorIfPublished(@PathVariable Long eventId);

}
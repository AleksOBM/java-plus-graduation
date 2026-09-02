package ru.practicum.aggregation.client.request;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.model.repository.EventRequestCount;

import java.util.List;

@FeignClient(
		name = "request-service",
		fallbackFactory = RequestFallbackFactory.class
)
public interface RequestClient {

	@GetMapping("/system/requests/users/{userId}/events/{eventId}")
	List<ParticipationRequestDto> findByUserIdAndEventId(@PathVariable @Positive Long userId,
	                                                     @PathVariable @Positive Long eventId);

	@PatchMapping("/system/requests/events/{eventId}/status")
	EventRequestStatusUpdateResult updateStatusRequest(
			@PathVariable Long eventId,
			@RequestParam Integer participantLimit,
			@RequestParam Boolean requestModeration,
			@RequestBody EventRequestStatusUpdateRequest request
	);

	@GetMapping("/system/requests/count")
	List<EventRequestCount> getConfirmedRequestsCount(@RequestParam List<Long> eventIds);
}

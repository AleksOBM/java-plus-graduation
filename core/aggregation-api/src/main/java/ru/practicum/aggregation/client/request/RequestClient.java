package ru.practicum.aggregation.client.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.enums.ParticipationStatus;

import java.util.List;

@FeignClient(
		name = "request-service",
		fallbackFactory = RequestFallbackFactory.class
)
public interface RequestClient {

	@GetMapping("/users/{userId}/requests/{eventId}")
	List<ParticipationRequestDto> findByEventId(@PathVariable @Positive Long userId,
	                                            @PathVariable @Positive Long eventId);

	@PatchMapping("/users/{userId}/requests/events/{eventId}/status")
	EventRequestStatusUpdateResult updateStatusRequest(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long eventId,
			@RequestBody @NotNull EventRequestStatusUpdateRequest request
	);

	@GetMapping("/admin/requests/count")
	List<EventRequestCount> getCount(@RequestParam List<Long> eventIds,
	                                 @RequestParam ParticipationStatus status);
}

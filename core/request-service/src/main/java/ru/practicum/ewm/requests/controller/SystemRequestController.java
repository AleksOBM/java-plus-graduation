package ru.practicum.ewm.requests.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/system/requests")
@RequiredArgsConstructor
public class SystemRequestController {

	private final RequestService requestService;

	/**
	 * Найти количество подтвержденных запросов на участие по ID событий
	 * @param eventIds ID событий
	 * @return List.of({@link EventRequestCount})
	 */
	@GetMapping("/count")
	public List<EventRequestCount> getConfirmedRequestsCount(@RequestParam List<Long> eventIds) {
		return requestService.getRequestsCount(eventIds);
	}

	@PatchMapping("/events/{eventId}/status")
	public EventRequestStatusUpdateResult updateStatusRequest(
			@PathVariable @Positive Long eventId,
			@RequestBody @NotNull EventRequestStatusUpdateRequest request
	) {
		return requestService.updateStatusRequest(eventId, request);
	}

	/**
	 *
	 * @param userId  id текущего пользователя
	 * @param eventId id события
	 * @return List<{@link ParticipationRequestDto}>
	 */
	@GetMapping("/users/{userId}/events/{eventId}")
	public List<ParticipationRequestDto> findByUserIdAndEventId(@PathVariable @Positive Long userId,
	                                                            @PathVariable @Positive Long eventId) {
		return requestService.findByEventId(userId, eventId);
	}

}

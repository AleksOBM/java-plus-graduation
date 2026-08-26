package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.aggregation.dto.request.EventRequestCount;
import ru.practicum.aggregation.enums.ParticipationStatus;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/requests")
@RequiredArgsConstructor
public class AdminRequestController {

	private final RequestService requestService;

	@GetMapping("/count")
	public List<EventRequestCount> getCount(@RequestParam List<Long> eventIds,
	                                        @RequestParam ParticipationStatus status) {
		return requestService.getRequestsCount(eventIds, status);
	}

}

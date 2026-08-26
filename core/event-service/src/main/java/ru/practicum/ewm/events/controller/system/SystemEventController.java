package ru.practicum.ewm.events.controller.system;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.aggregation.dto.event.response.EventFullDto;
import ru.practicum.ewm.events.service.event.EventService;

@Slf4j
@RestController
@RequestMapping("/system/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemEventController {

	EventService eventService;

	@GetMapping("/{eventId}")
	EventFullDto systemFindEventById(@PathVariable @Positive Long eventId) {
		return eventService.findEventById(eventId);
	}
}

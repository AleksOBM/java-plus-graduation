package ru.practicum.ewm.events.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.event.output.EventShortDto;
import ru.practicum.ewm.events.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events/recommendations")
@RequiredArgsConstructor
public class UserRecomendationsController {

	private final EventService eventService;

	@GetMapping
	public List<EventShortDto> getRecommendationsForUser(@RequestHeader("X-EWM-USER-ID")
	                                                     long userId,
	                                                     @RequestParam(defaultValue = "10", required = false)
	                                                     Integer maxResults,
	                                                     @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Получение рекомендации мероприятий для пользователя.
				{} {}""", request.getMethod(), request.getRequestURI());

		return eventService.getRecommendationsForUser(userId, maxResults);
	}

}

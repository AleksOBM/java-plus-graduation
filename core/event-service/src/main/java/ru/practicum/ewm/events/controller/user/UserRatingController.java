package ru.practicum.ewm.events.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.client.dto.ActionType;
import ru.practicum.stats.client.dto.UserActionDto;
import ru.practicum.stats.client.grpc.CollectorClient;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class UserRatingController {

	private final CollectorClient collectorClient;

	@PutMapping("/{eventId}/like")
	public void addLike(@PathVariable @Positive Long eventId, @RequestHeader("X-EWM-USER-ID") Long userId) {
		log.info("Пользователь {} поставил лайк событию {}", userId, eventId);
		collectorClient.collectUserAction(
				UserActionDto.builder()
						.userId(userId)
						.eventId(eventId)
						.actionType(ActionType.LIKE)
						.timestamp(LocalDateTime.now())
						.build()
		);
	}
}

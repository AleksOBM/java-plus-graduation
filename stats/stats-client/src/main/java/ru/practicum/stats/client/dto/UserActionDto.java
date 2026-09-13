package ru.practicum.stats.client.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserActionDto(
		long userId,
		long eventId,
		ActionType actionType,
		LocalDateTime timestamp
) {
}

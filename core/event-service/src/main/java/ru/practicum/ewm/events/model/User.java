package ru.practicum.ewm.events.model;

import lombok.Builder;

@Builder
public record User(
		Long id,
		String name,
		String email
) {
}

package ru.practicum.aggregation.dto.participation.output;

import lombok.Builder;
import ru.practicum.aggregation.enums.ParticipationStatus;

import java.time.LocalDateTime;

/**
 * @apiNote
 * {@link Long} id<br/>
 * {@link String} created<br/>
 * {@link Long} event<br/>
 * {@link Long} requester<br/>
 * {@link ParticipationStatus} status
 */
@Builder
public record ParticipationRequestDto(
		Long id,
		LocalDateTime created,
		Long event,
		Long requester,
		ParticipationStatus status
) {
}

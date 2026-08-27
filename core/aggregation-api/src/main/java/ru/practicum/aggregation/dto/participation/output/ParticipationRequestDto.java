package ru.practicum.aggregation.dto.participation.output;

import lombok.Builder;
import ru.practicum.aggregation.enums.ParticipationStatus;

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
		String created,
		Long event,
		Long requester,
		ParticipationStatus status
) {
}

package ru.practicum.aggregation.dto.request;

import lombok.Builder;
import ru.practicum.aggregation.enums.ParticipationStatus;

/**
 * @apiNote
 * <code>{@link Long} id</code> <br/>
 * <code>{@link String} created</code> <br/>
 * <code>{@link Long} event</code> <br/>
 * <code>{@link Long} requester</code> <br/>
 * <code>{@link ParticipationStatus} status</code> <br/>
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

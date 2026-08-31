package ru.practicum.aggregation.dto.participation.output;

import lombok.Builder;

import java.util.List;

/**
 * @apiNote
 * {@link List}<code><</code>{@link ParticipationRequestDto}<code>></code> confirmedRequests</code> <br/>
 * {@link List}<code><</code>{@link ParticipationRequestDto}<code>></code> rejectedRequests</code>
 */
@Builder
public record EventRequestStatusUpdateResult(
		List<ParticipationRequestDto> confirmedRequests,
		List<ParticipationRequestDto> rejectedRequests
) {
}

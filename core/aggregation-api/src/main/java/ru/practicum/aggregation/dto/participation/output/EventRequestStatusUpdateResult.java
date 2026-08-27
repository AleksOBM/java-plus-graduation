package ru.practicum.aggregation.dto.participation.output;

import lombok.Builder;

import java.util.List;

/**
 * @apiNote
 * <code>List<{@link ParticipationRequestDto}> confirmedRequests</code> <br/>
 * <code>List<{@link ParticipationRequestDto}> rejectedRequests</code> <br/>
 */
@Builder
public record EventRequestStatusUpdateResult(
		List<ParticipationRequestDto> confirmedRequests,
		List<ParticipationRequestDto> rejectedRequests
) {
}

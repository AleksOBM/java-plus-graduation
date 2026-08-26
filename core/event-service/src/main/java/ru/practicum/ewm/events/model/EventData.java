package ru.practicum.ewm.events.model;

import lombok.Builder;
import ru.practicum.aggregation.dto.user.UserShortDto;

/**
 * @apiNote
 * <code>UserShortDto initiator</code> <br/>
 * <code>long confirmedRequests</code> <br/>
 * <code>long views</code>
 */
@Builder
public record EventData(
		UserShortDto initiator,
		long confirmedRequests,
		long views
) {
}
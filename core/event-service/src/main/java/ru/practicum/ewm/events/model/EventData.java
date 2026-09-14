package ru.practicum.ewm.events.model;

import lombok.Builder;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

/**
 * @apiNote
 * <code>UserShortDto</code> initiator <br/>
 * <code>long</code> confirmedRequests <br/>
 * <code>double</code> rating
 */
@Builder
public record EventData(
		UserShortDto initiator,
		long confirmedRequests,
		double rating
) {
}
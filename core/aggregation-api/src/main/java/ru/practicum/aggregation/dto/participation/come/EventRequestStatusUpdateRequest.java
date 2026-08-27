package ru.practicum.aggregation.dto.participation.come;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.aggregation.enums.RequestUpdateStatus;

import java.util.List;

/**
 * @apiNote
 * {@link List}<code><</code>{@link Long}<code>></code> requestIds <br/>
 * {@link RequestUpdateStatus} status
 */
@Builder
public record EventRequestStatusUpdateRequest(

		List<Long> requestIds,

		@NotNull
		RequestUpdateStatus status
) {
}

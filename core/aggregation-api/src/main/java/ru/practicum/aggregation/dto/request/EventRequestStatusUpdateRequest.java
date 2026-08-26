package ru.practicum.aggregation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.aggregation.enums.RequestUpdateStatus;

import java.util.List;

/**
 * @apiNote
 * <code>List<{@link Long}> requestIds</code> <br/>
 * <code>{@link RequestUpdateStatus} status</code>
 */
@Builder
public record EventRequestStatusUpdateRequest(

		List<Long> requestIds,

		@NotNull
		RequestUpdateStatus status
) {
}

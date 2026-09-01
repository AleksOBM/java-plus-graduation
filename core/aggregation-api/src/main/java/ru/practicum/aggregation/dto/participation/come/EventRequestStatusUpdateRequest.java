package ru.practicum.aggregation.dto.participation.come;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import ru.practicum.aggregation.enums.RequestUpdateStatus;

import java.util.List;

/** Новый статус для заявок на участие в событии текущего пользователя
 * @apiNote {@link List}<code><</code>{@link Long}<code>></code> requestIds <br/>
 * {@link RequestUpdateStatus} status
 */
@Builder
public record EventRequestStatusUpdateRequest(

		@NotEmpty
		List<@Positive(message = "id запроса на участие должен быть положительным числом") Long> requestIds,

		@NotNull
		RequestUpdateStatus status
) {
}

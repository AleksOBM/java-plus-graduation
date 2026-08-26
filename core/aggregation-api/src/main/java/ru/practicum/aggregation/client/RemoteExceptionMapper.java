package ru.practicum.aggregation.client;

import feign.FeignException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.aggregation.error.exception.processing.EventProcessingException;
import ru.practicum.aggregation.error.exception.processing.RequestProcessingException;
import ru.practicum.aggregation.error.exception.processing.UserProcessingException;
import ru.practicum.aggregation.error.exception.unavailable.EventServiceUnavailableException;
import ru.practicum.aggregation.error.exception.unavailable.RequestServiceUnavailableException;
import ru.practicum.aggregation.error.exception.unavailable.UserServiceUnavailableException;

@UtilityClass
public class RemoteExceptionMapper {

	public RuntimeException mapUserException(@NonNull Throwable cause) {
		if (cause instanceof FeignException ex) {
			return switch (ex.status()) {
				case 404, 409 -> new UserProcessingException(ex.getMessage());
				default -> new UserServiceUnavailableException(ex);
			};
		}
		return new UserServiceUnavailableException(cause);
	}

	public RuntimeException mapEventException(@NonNull Throwable cause) {
		if (cause instanceof FeignException ex) {
			return switch (ex.status()) {
				case 400, 404, 409 -> new EventProcessingException(ex.getMessage());
				default -> new EventServiceUnavailableException(ex);
			};
		}
		return new EventServiceUnavailableException(cause);
	}

	public RuntimeException mapRequestException(@NonNull Throwable cause) {
		if (cause instanceof FeignException ex) {
			return switch (ex.status()) {
				case 404, 409 -> new RequestProcessingException(ex.getMessage());
				default -> new RequestServiceUnavailableException(ex);
			};
		}
		return new RequestServiceUnavailableException(cause);
	}

}

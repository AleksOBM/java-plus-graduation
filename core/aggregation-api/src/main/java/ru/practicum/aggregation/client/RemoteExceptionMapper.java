package ru.practicum.aggregation.client;

import feign.FeignException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.aggregation.error.exception.bussines.cause.BadRequestException;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.error.exception.unavailable.EventServiceUnavailableException;
import ru.practicum.aggregation.error.exception.unavailable.RequestServiceUnavailableException;
import ru.practicum.aggregation.error.exception.unavailable.UserServiceUnavailableException;

@UtilityClass
public class RemoteExceptionMapper {

	public RuntimeException mapUserException(@NonNull Throwable cause) {
		if (cause instanceof FeignException ex) {
			return switch (ex.status()) {
				case 400 -> new BadRequestException(ex.getMessage());
				case 404 -> new NotFoundException(ex.getMessage());
				case 409 -> new ConflictException(ex.getMessage());
				default -> new EventServiceUnavailableException(ex);
			};
		}
		return new UserServiceUnavailableException(cause);
	}

	public RuntimeException mapEventException(@NonNull Throwable cause) {
		if (cause instanceof FeignException ex) {
			return switch (ex.status()) {
				case 400 -> new BadRequestException(ex.getMessage());
				case 404 -> new NotFoundException(ex.getMessage());
				case 409 -> new ConflictException(ex.getMessage());
				default -> new EventServiceUnavailableException(ex);
			};
		}
		return new EventServiceUnavailableException(cause);
	}

	public RuntimeException mapRequestException(@NonNull Throwable cause) {
		if (cause instanceof FeignException ex) {
			return switch (ex.status()) {
				case 400 -> new BadRequestException(ex.getMessage());
				case 404 -> new NotFoundException(ex.getMessage());
				case 409 -> new ConflictException(ex.getMessage());
				default -> new EventServiceUnavailableException(ex);
			};
		}
		return new RequestServiceUnavailableException(cause);
	}

}

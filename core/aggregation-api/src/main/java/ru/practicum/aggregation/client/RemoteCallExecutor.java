package ru.practicum.aggregation.client;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.error.exception.bussines.ServiceProcessingException;
import ru.practicum.aggregation.error.exception.unavailable.ServiceUnavailableException;

import java.util.function.Supplier;

@UtilityClass
public class RemoteCallExecutor {

	@NonNull
	public <T> RemoteCallResult<T> execute(@NonNull Supplier<T> action) {
		try {
			return new RemoteCallResult.Success<>(action.get());

		} catch (ServiceProcessingException ex) {
			return new RemoteCallResult.Failure<>(ex);

		} catch (ServiceUnavailableException ex) {
			return new RemoteCallResult.Degraded<>(ex);
		}
	}

	@NonNull
	public static RemoteCallResult<Void> executeVoid(@NonNull Runnable function) {
		try {
			function.run();
			return new RemoteCallResult.Success<>(null);

		} catch (ServiceProcessingException ex) {
			return new RemoteCallResult.Failure<>(ex);

		} catch (ServiceUnavailableException ex) {
			return new RemoteCallResult.Degraded<>(ex);
		}
	}
}

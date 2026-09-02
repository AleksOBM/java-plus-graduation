package ru.practicum.aggregation.client;

import ru.practicum.aggregation.error.exception.bussines.ServiceProcessingException;

@SuppressWarnings("unused")
public sealed interface RemoteCallResult<T>
		permits RemoteCallResult.Success,
		RemoteCallResult.Failure,
		RemoteCallResult.Degraded {

	record Success<T>(T value)
			implements RemoteCallResult<T> {
	}

	record Failure<T>(ServiceProcessingException ex)
			implements RemoteCallResult<T> {
	}

	record Degraded<T>(Throwable cause)
			implements RemoteCallResult<T> {
	}
}
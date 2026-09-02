package ru.practicum.aggregation.error.exception.unavailable;

public class ServiceUnavailableException extends RuntimeException {
	public ServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}

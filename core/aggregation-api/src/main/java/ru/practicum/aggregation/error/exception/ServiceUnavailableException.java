package ru.practicum.aggregation.error.exception;

public class ServiceUnavailableException extends RuntimeException {
	public ServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}

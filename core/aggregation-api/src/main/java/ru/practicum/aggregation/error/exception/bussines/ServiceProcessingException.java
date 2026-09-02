package ru.practicum.aggregation.error.exception.bussines;

public class ServiceProcessingException extends RuntimeException {
	public ServiceProcessingException(String message) {
		super(message);
	}
}

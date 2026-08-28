package ru.practicum.aggregation.error.exception.bussines.cause;

import ru.practicum.aggregation.error.exception.bussines.ServiceProcessingException;

public class ConflictException extends ServiceProcessingException {

	public ConflictException(String message) {
		super(message);
	}
}

package ru.practicum.aggregation.error.exception.bussines.cause;

import ru.practicum.aggregation.error.exception.bussines.ServiceProcessingException;

public class NotFoundException extends ServiceProcessingException {

	public NotFoundException(String message) {
		super(message);
	}
}

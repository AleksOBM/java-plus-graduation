package ru.practicum.aggregation.error.exception.processing;

import ru.practicum.aggregation.error.exception.ServiceProcessingException;

public class RequestProcessingException extends ServiceProcessingException {

	public RequestProcessingException(String message) {
		super(message);
	}

}

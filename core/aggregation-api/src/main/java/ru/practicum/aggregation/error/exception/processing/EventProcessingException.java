package ru.practicum.aggregation.error.exception.processing;

import ru.practicum.aggregation.error.exception.ServiceProcessingException;

public class EventProcessingException extends ServiceProcessingException {

	public EventProcessingException(String message) {
		super(message);
	}

}

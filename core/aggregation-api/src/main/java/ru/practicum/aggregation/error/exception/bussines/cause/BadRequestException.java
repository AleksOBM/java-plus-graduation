package ru.practicum.aggregation.error.exception.bussines.cause;

import ru.practicum.aggregation.error.exception.bussines.ServiceProcessingException;

public class BadRequestException extends ServiceProcessingException {
		public BadRequestException(String message) {
			super(message);
		}
}

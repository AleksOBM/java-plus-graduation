package ru.practicum.aggregation.error.exception.unavailable;

public class StatsServerUnavailableException extends ServiceUnavailableException {
	public StatsServerUnavailableException(String message, Throwable cause) {
		super(message,  cause);
	}
}

package ru.practicum.aggregation.error.exception.unavailable;

public class EventServiceUnavailableException extends ServiceUnavailableException {

	public EventServiceUnavailableException(Throwable cause) {
		super("""
						Сервис событий временно недоступен.
						Повторите попытку позже.
						""",
				cause
		);
	}

}

package ru.practicum.aggregation.error.exception.unavailable;

import ru.practicum.aggregation.error.exception.ServiceUnavailableException;

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

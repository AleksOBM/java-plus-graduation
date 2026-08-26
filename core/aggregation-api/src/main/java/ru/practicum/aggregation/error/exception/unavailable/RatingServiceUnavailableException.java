package ru.practicum.aggregation.error.exception.unavailable;

import ru.practicum.aggregation.error.exception.ServiceUnavailableException;

public class RatingServiceUnavailableException extends ServiceUnavailableException {

	public RatingServiceUnavailableException(Throwable cause) {
		super("""
						Сервис рейтингов временно недоступен.
						Повторите попытку позже.
						""",
				cause
		);
	}

}

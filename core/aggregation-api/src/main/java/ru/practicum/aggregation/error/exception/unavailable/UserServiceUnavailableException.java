package ru.practicum.aggregation.error.exception.unavailable;

import ru.practicum.aggregation.error.exception.ServiceUnavailableException;

public class UserServiceUnavailableException extends ServiceUnavailableException {

	public UserServiceUnavailableException(Throwable cause) {
		super("""
						Сервис пользователей временно недоступен.
						Повторите попытку позже.
						""",
				cause
		);
	}

}

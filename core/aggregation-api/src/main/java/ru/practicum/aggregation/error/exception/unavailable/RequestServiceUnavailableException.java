package ru.practicum.aggregation.error.exception.unavailable;

public class RequestServiceUnavailableException extends ServiceUnavailableException {

	public RequestServiceUnavailableException(Throwable cause) {
		super("""
						Сервис запросов на участие временно недоступен.
						Повторите попытку позже.
						""",
				cause
		);
	}

}

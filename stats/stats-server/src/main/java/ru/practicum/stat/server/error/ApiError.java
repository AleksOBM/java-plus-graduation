package ru.practicum.stat.server.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiError {
	private HttpStatus status;
	private String reason;
	private String message;
	private String errors;
	private LocalDateTime timestamp = LocalDateTime.now();

	public ApiError(HttpStatus status, String reason, String message, String stackTrace) {
		this.status = status;
		this.reason = reason;
		this.message = message;
		this.errors = stackTrace;
	}
}


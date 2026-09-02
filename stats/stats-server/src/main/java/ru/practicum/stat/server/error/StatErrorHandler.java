package ru.practicum.stat.server.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class StatErrorHandler {

	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			IllegalArgumentException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleBadRequest(@NonNull final Exception e) {
		log.error("400 Bad Request: {}", e.getMessage());
		return new ApiError(
				HttpStatus.BAD_REQUEST,
				"Bad Request",
				e.getMessage(),
				getStackTrace(e)
		);
	}

	private String getStackTrace(@NonNull Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiError handleException(Exception e) {
		log.info("500 {}", e.getMessage(), e);
		String stackTrace = getStackTrace(e);
		return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
				"Error ....", e.getMessage(), stackTrace);
	}
}

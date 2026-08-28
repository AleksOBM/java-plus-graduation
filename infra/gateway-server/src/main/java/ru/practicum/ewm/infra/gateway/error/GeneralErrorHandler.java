package ru.practicum.ewm.infra.gateway.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GeneralErrorHandler {

	@ExceptionHandler
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public ApiError handleNoResourceFoundException(NoResourceFoundException ex) {
		log.info("500 Internal Server Error (NoResourceFoundException) {}", ex.getMessage(), ex);
		String stackTrace = getStackTrace(ex);
		return ApiError.builder()
				.status(INTERNAL_SERVER_ERROR)
				.reason("Resource not found")
				.message(ex.getMessage())
				.errors(stackTrace)
				.timestamp(LocalDateTime.now())
				.build();
	}

	private String getStackTrace(@NonNull Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}

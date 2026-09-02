package ru.practicum.aggregation.error;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.error.exception.stats.HitRequestException;
import ru.practicum.aggregation.error.exception.stats.StatsResponseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GeneralErrorHandler {

	@ExceptionHandler
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public ApiError handleSomeError(final Exception ex) {
		String stackTrace = getStackTrace(ex);
		return ApiError.builder()
				.status(INTERNAL_SERVER_ERROR)
				.reason("Непредвиденная ошибка")
				.message(ex.getMessage())
				.errors(stackTrace)
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public ApiError handleHitRequestException(HitRequestException ex) {
		log.info("500 {}", ex.getMessage(), ex);
		String stackTrace = getStackTrace(ex);
		return ApiError.builder()
				.status(INTERNAL_SERVER_ERROR)
				.reason("Stat-server error ....")
				.message(ex.getMessage())
				.errors(stackTrace)
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public ApiError handleStatResponseException(StatsResponseException ex) {
		log.info("500 {}", ex.getMessage(), ex);
		String stackTrace = getStackTrace(ex);
		return ApiError.builder()
				.status(INTERNAL_SERVER_ERROR)
				.reason("Stat-server error ....")
				.message(ex.getMessage())
				.errors(stackTrace)
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(BAD_REQUEST)
	public ApiError handleMissingServletRequestParameter(
			@NonNull final MissingServletRequestParameterException ex) {
		log.warn("400 Bad Request (MissingServletRequestParameter): {}", ex.getMessage());
		String stackTrace = getStackTrace(ex);
		return ApiError.builder()
				.status(BAD_REQUEST)
				.reason("MissingServletRequestParameter")
				.message(ex.getMessage())
				.errors(stackTrace)
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(BAD_REQUEST)
	public ApiError handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex) {
		log.warn("400 Bad Request (ArgumentNotValid): {}", ex.getMessage());
		return ApiError.builder()
				.status(BAD_REQUEST)
				.reason("Incorrectly made request.")
				.message("Field: " + Objects.requireNonNull(ex.getBindingResult().getFieldError()).getField()
						+ ". Error: " + ex.getBindingResult().getFieldError().getDefaultMessage())
				.errors(getStackTrace(ex))
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(BAD_REQUEST)
	public ApiError handleHandlerMethodValidationException(@NonNull HandlerMethodValidationException ex) {
		log.warn("400 Bad Request (HandlerMethodValidationException): {}", ex.getMessage());
		return ApiError.builder()
				.status(BAD_REQUEST)
				.reason("HandlerMethodValidationException")
				.message(ex.getMessage())
				.errors(getStackTrace(ex))
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(BAD_REQUEST)
	public ApiError handleIllegalArgumentException(@NonNull IllegalArgumentException ex) {
		log.warn("400 Bad Request (IllegalArgument): {}", ex.getMessage());
		return ApiError.builder()
				.status(BAD_REQUEST)
				.reason("Incorrectly made request.")
				.message(ex.getMessage())
				.errors(getStackTrace(ex))
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(BAD_REQUEST)
	public ApiError handleValidationException(@NonNull ValidationException ex) {
		log.warn("400 Bad Request (ValidationException): {}", ex.getMessage());
		return ApiError.builder()
				.status(BAD_REQUEST)
				.reason("Incorrectly made request.")
				.message(ex.getMessage())
				.errors(getStackTrace(ex))
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(NOT_FOUND)
	public ApiError handleNotFound(@NonNull NotFoundException ex) {
		log.warn("404 Not Found: {}", ex.getMessage());
		return ApiError.builder()
				.status(NOT_FOUND)
				.reason("The required object was not found.")
				.message(ex.getMessage())
				.errors(getStackTrace(ex))
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(CONFLICT)
	public ApiError handleConflict(@NonNull ConflictException ex) {
		log.warn("409 Conflict: {}", ex.getMessage());
		return ApiError.builder()
				.status(CONFLICT)
				.reason("For the requested operation the conditions are not met.")
				.message(ex.getMessage())
				.errors(getStackTrace(ex))
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler
	@ResponseStatus(CONFLICT)
	public ApiError handleHttpMessageNotReadableException(@NonNull final HttpMessageNotReadableException ex) {
		log.warn("409 CONFLICT: Required request body is missing or invalid");
		return ApiError.builder()
				.status(CONFLICT)
				.reason("Incorrectly made request.")
				.message(ex.getMessage())
				.errors(getStackTrace(ex))
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

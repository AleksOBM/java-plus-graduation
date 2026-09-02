package ru.practicum.aggregation.error.exception.stats;

public class StatsResponseException extends RuntimeException {

	private static final String MESSAGE = "Не удалось получить статистику";

	public StatsResponseException() {
		super(MESSAGE);
	}
}

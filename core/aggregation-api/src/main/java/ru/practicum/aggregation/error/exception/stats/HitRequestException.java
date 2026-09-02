package ru.practicum.aggregation.error.exception.stats;

public class HitRequestException extends RuntimeException {

	public HitRequestException(Exception ex) {
		super("Отправка статистики не удалась " + ex.getMessage(), ex);
	}
}

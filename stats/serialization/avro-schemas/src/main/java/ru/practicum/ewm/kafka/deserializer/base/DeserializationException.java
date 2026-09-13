package ru.practicum.ewm.kafka.deserializer.base;

public class DeserializationException extends RuntimeException {

	@SuppressWarnings("unused")
	public DeserializationException(String message, Exception e) {
		super(message);
	}
}

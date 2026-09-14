package ru.practicum.ewm.kafka.deserializer;

import ru.practicum.ewm.kafka.deserializer.base.BaseAvroDeserializer;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;

@SuppressWarnings("unused")
public class EventsSimilarityDeserializer extends BaseAvroDeserializer<EventsSimilarityAvro> {

	public EventsSimilarityDeserializer() {
		super(EventsSimilarityAvro.getClassSchema());
	}
}

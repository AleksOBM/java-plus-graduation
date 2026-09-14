package ru.practicum.ewm.kafka.deserializer;

import ru.practicum.ewm.kafka.deserializer.base.BaseAvroDeserializer;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@SuppressWarnings("unused")
public class UserActionsDeserializer extends BaseAvroDeserializer<UserActionAvro> {

	public UserActionsDeserializer() {
		super(UserActionAvro.getClassSchema());
	}
}

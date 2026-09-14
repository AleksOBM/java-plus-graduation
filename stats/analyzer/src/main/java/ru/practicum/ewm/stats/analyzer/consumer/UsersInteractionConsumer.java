package ru.practicum.ewm.stats.analyzer.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.service.InteractionsService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
public class UsersInteractionConsumer extends BaseKafkaProcessor<UserActionAvro> {

	private final InteractionsService service;

	public UsersInteractionConsumer(@Value("${kafka.topics.users-interaction}") String topic,
	                                KafkaConsumer<Long, UserActionAvro> consumer,
	                                InteractionsService service) {
		super(topic, consumer);
		this.service = service;
	}

	@Override
	protected void processRecord(UserActionAvro record) {
		service.addInteraction(record);
	}
}

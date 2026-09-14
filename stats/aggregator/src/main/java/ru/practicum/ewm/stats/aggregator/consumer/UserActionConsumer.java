package ru.practicum.ewm.stats.aggregator.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.aggregator.service.SimilarityService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
public class UserActionConsumer extends BaseKafkaProcessor<UserActionAvro> {

	private final SimilarityService service;

	public UserActionConsumer(@Value("${kafka.topics.users-interaction}") String topic,
	                          KafkaConsumer<Long, UserActionAvro> consumer,
	                          SimilarityService service) {
		super(topic, consumer);
		this.service = service;
	}

	@Override
	protected void processRecord(UserActionAvro record) {
		service.collectSimilarity(record);
	}
}

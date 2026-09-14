package ru.practicum.ewm.stats.analyzer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.service.SimilarityService;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;

@Slf4j
@Component
public class EventsSimilarityComsumer extends BaseKafkaProcessor<EventsSimilarityAvro> {

	private final SimilarityService service;

	public EventsSimilarityComsumer(@Value("${kafka.topics.events-similarity}") String topic,
	                                KafkaConsumer<Long, EventsSimilarityAvro> consumer,
	                                SimilarityService service) {
		super(topic, consumer);
		this.service = service;
	}

	@Override
	protected void processRecord(EventsSimilarityAvro record) {
		service.addSimilarity(record);
	}
}

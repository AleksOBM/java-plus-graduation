package ru.practicum.ewm.stats.aggregator.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsSimilarityProducer {

	@Value("${kafka.topics.events-similarity}")
	private String topic;

	private final Producer<Long, EventsSimilarityAvro> producer;

	public <T extends EventsSimilarityAvro> CompletableFuture<RecordMetadata> sendMessage(
			Long key,
			@Nonnull T similarity
	) {
		ProducerRecord<Long, EventsSimilarityAvro> record = new ProducerRecord<>(
				topic, null, key, similarity);

		CompletableFuture<RecordMetadata> future = new CompletableFuture<>();

		producer.send(record, (metadata, exception) -> {
			if (exception != null) {
				future.completeExceptionally(exception);
			} else {
				future.complete(metadata);
			}
		});

		return future;
	}

}

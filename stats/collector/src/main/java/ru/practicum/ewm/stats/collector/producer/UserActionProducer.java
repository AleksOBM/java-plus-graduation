package ru.practicum.ewm.stats.collector.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProducer {

	@Value("${kafka.topics.users-interaction}")
	private String topic;

	private final Producer<Long, UserActionAvro> producer;

	public <T extends UserActionAvro> CompletableFuture<RecordMetadata> sendMessage(Long key,
	                                                                                @Nonnull T userAction) {
		ProducerRecord<Long, UserActionAvro> record = new ProducerRecord<>(
				topic, null, key, userAction);

		CompletableFuture<RecordMetadata> future = new CompletableFuture<>();

		log.info("Отправляю сообщение:\n{}", record.value());
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

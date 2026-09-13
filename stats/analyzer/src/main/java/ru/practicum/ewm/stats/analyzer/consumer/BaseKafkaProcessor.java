package ru.practicum.ewm.stats.analyzer.consumer;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public abstract class BaseKafkaProcessor<T> implements Runnable {

	final String topic;

	private final KafkaConsumer<Long, T> consumer;

	private final Map<TopicPartition, OffsetAndMetadata> lastOffsets = new HashMap<>();

	private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

	@Getter
	private volatile boolean running = false;

	public BaseKafkaProcessor(String topic, KafkaConsumer<Long, T> consumer) {
		this.topic = topic;
		this.consumer = consumer;
	}

	@PreDestroy
	public void shutdown() {
		log.info("Получил сигнал остановки");
		running = false;
		consumer.wakeup();
	}

	@Override
	public void run() {
		running = true;

		log.info("Подписка на топик: {}", topic);
		consumer.subscribe(Collections.singleton(topic));

		try {
			pollLoop();

		} catch (WakeupException ignored) {
			log.info("Получил инструкцию wakeup");
		} catch (Exception e) {
			log.error("Ошибка во время обработки событий от хаба", e);
		} finally {
			log.info("Остановка poll loop");
			running = false;

			try {
				if (!lastOffsets.isEmpty()) {
					log.info("Регистрация последнего смещения");
					consumer.commitSync(lastOffsets);
				}

			} finally {
				log.info("Остановка консьюмера");
				consumer.close();
			}
		}
	}

	private void pollLoop() {
		int iteration = 1;
		while (running) {
			log.debug("Проверка источника событий хаба");
			ConsumerRecords<Long, T> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

			if (records.isEmpty()) {
				log.debug("Источник событий хаба пуст");
				continue;
			}

			log.info("Итерация {}. Батч из {} записей получен. Начинаем обработку.",
					iteration++, records.count());

			int recordNumber = 1;
			for (ConsumerRecord<Long, T> record : records) {

				log.info("""
								Обработка действия пользователя №{}:
								{
								  "topic": "{}",
								  "partition": "{}",
								  "offset": "{}",
								  "timestamp": "{}"
								}""",
						recordNumber++,
						record.topic(),
						record.partition(),
						record.offset(),
						TimestampUtils.toLocalDateTime(record.timestamp())
				);

				try {
					processRecord(record.value());

					log.debug("Сохранение смещения локально");
					lastOffsets.put(
							new TopicPartition(record.topic(), record.partition()),
							new OffsetAndMetadata(record.offset() + 1)
					);

				} catch (Exception e) {
					log.error("Ошибка обновления состояния:\n{}", record.value(), e);
					throw e;
				}
			}
		}
	}

	protected abstract void processRecord(T record);
}
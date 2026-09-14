package ru.practicum.ewm.stats.aggregator.cofig;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
public class AggregatorConfig {

	@Bean
	@ConfigurationProperties("kafka.user-action-consumer-config")
	public Properties usersInteractionConsumerConfig() {
		return new Properties();
	}

	@Bean
	@ConfigurationProperties("kafka.events-similarity-producer-config")
	public Properties eventsSimilarityProducerConfig() {
		return new Properties();
	}

	@Bean(destroyMethod = "")
	public KafkaConsumer<Long, UserActionAvro> usersInteractionKafkaConsumer(
			@Qualifier("usersInteractionConsumerConfig") Properties config
	) {
		return new KafkaConsumer<>(config);
	}

	@Bean
	public KafkaProducer<Long, EventsSimilarityAvro> eventsSimilarityKafkaProducer(
			@Qualifier("eventsSimilarityProducerConfig") Properties config
	) {
		return new KafkaProducer<>(config);
	}
}

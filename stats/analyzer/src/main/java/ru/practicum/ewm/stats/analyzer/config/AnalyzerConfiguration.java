package ru.practicum.ewm.stats.analyzer.config;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
public class AnalyzerConfiguration {

	@Bean
	@ConfigurationProperties("kafka.users-interaction-consumer-config")
	public Properties usersInteractionConsumerConfig() {
		return new Properties();
	}

	@Bean
	@ConfigurationProperties("kafka.events-similarity-consumer-config")
	public Properties eventsSimilarityConsumerConfig() {
		return new Properties();
	}

	@Bean(destroyMethod = "")
	public KafkaConsumer<Long, UserActionAvro> usersInteractionKafkaConsumer(
			@Qualifier("usersInteractionConsumerConfig") Properties config
	) {
		return new KafkaConsumer<>(config);
	}

	@Bean(destroyMethod = "")
	public KafkaConsumer<Long, EventsSimilarityAvro> eventsSimilarityKafkaConsumer(
			@Qualifier("eventsSimilarityConsumerConfig") Properties config
	) {
		return new KafkaConsumer<>(config);
	}
}

package ru.practicum.ewm.stats.collector.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
public class CollectorConfiguration {

	@Bean
	@ConfigurationProperties("kafka.user-action-producer-config")
	public Properties userActionProducerConfig() {
		return new Properties();
	}

	@Bean
	public KafkaProducer<Long, UserActionAvro> userActionKafkaProducer(
			@Qualifier("userActionProducerConfig") Properties config
	) {
		return new KafkaProducer<>(config);
	}

}

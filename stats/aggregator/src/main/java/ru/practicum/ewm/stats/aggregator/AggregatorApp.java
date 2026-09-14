package ru.practicum.ewm.stats.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AggregatorApp {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication
				.run(AggregatorApp.class, args);

		final AggregatorRunner runner = context.getBean(AggregatorRunner.class);
		runner.run(args);
	}
}

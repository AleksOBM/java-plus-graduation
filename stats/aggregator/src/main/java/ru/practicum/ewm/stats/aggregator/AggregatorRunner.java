package ru.practicum.ewm.stats.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.aggregator.consumer.UserActionConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorRunner implements CommandLineRunner {

	private final UserActionConsumer userActionConsumer;

	private int cycleNumber = 0;

	@Override
	public void run(String... args) {
		log.info("Цикл запуска №{}", ++cycleNumber);

		if (!userActionConsumer.isRunning()) {
			log.info("Запускаем UserActionConsumer в текущем потоке");
			userActionConsumer.run();
		} else {
			log.info("EventsSimilarityClient уже запущен");
		}
	}

}

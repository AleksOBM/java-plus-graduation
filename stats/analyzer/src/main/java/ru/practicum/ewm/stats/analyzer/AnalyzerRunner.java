package ru.practicum.ewm.stats.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.consumer.EventsSimilarityComsumer;
import ru.practicum.ewm.stats.analyzer.consumer.UsersInteractionConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

	private final EventsSimilarityComsumer eventsSimilarityComsumer;
	private final UsersInteractionConsumer usersInteractionConsumer;

	private int cycleNumber = 0;

	@Override
	public void run(String... args) {

		log.info("Цикл запуска №{}", ++cycleNumber);

		if (!eventsSimilarityComsumer.isRunning()) {
			Thread EventsSimilarityThread = new Thread(eventsSimilarityComsumer);
			EventsSimilarityThread.setName("EventsSimilarityComsumerThread");

			log.info("Запускаем EventsSimilarityComsumer в отдельном потоке");
			EventsSimilarityThread.start();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} else {
			log.info("EventsSimilarityComsumer уже запущен");
		}

		if (!usersInteractionConsumer.isRunning()) {

			if (!eventsSimilarityComsumer.isRunning()) {
				log.info("UsersInteractionConsumer не запущен");
				return;
			}
			log.info("Запускаем UsersInteractionConsumer в текущем потоке");
			usersInteractionConsumer.run();
		} else {
			log.info("UsersInteractionConsumer уже запущен");
		}
	}

}

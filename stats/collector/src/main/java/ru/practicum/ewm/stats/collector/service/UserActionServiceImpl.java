package ru.practicum.ewm.stats.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.collector.producer.UserActionProducer;
import ru.practicum.ewm.stats.collector.mapper.UserActionMapper;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

	private final UserActionProducer client;

	@Override
	public void collectUserAction(UserActionProto proto) {
		UserActionAvro avro = UserActionMapper.toAvro(proto);

		CompletableFuture<RecordMetadata> result = client.sendMessage(avro.getEventId(), avro);
		result.whenComplete((record, throwable) -> {
			if (throwable != null) {
				log.error("Ошибка отправки действия пользователя {}:\n {}",
						avro.getActionType(), throwable.getMessage(), throwable);
			} else {
				log.info("{} отправлен по адресу:\n topic={}, key={}, partition={}, offset={}",
						avro.getActionType(),
						record.topic(),
						avro.getEventId(),
						record.partition(),
						record.offset()
				);
			}
		});
	}

}

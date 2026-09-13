package ru.practicum.ewm.stats.analyzer.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.analyzer.mapper.InteractionMapper;
import ru.practicum.ewm.stats.analyzer.repository.InteractionsRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractionsServiceImpl implements InteractionsService {

	InteractionsRepository repository;

	@Override
	public void addInteraction(UserActionAvro avro) {

		var interaction = InteractionMapper.toEntity(avro);

		long userId = avro.getUserId();
		long eventId = avro.getEventId();
		var actionType = avro.getActionType();

		log.info("""
						Сохраняю новое действие пользователя:
						{
							"userId": {},
							"eventId": {},
							"actionType": {},
							"timestamp": {}
						}
						""",
				userId,
				eventId,
				actionType,
				TimestampUtils.toLocalDateTime(avro.getTimestamp())
		);

		repository.findByUserIdAndEventId(userId, eventId)
				.ifPresentOrElse(inter -> {
							interaction.setId(inter.getId());

							// Сетим максимальное
							var updated = interaction.toBuilder()
									.actionWeight(interaction.getActionWeight().max(inter.getActionWeight()))
									.build();

							repository.save(updated);
						},
						() -> repository.save(interaction)
				);
	}

	@Override
	public Stream<RecommendedEventProto> getInteractionsByEventIds(Collection<Long> eventIds) {
		return repository.aggregateActionWeightsByEvent(new HashSet<>(eventIds))
				.map(eventWeightSum -> RecommendedEventProto.newBuilder()
						.setEventId(eventWeightSum.eventId())
						.setScore(eventWeightSum.score().doubleValue())
						.build());
	}

}

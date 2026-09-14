package ru.practicum.ewm.stats.analyzer.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.analyzer.entity.Interaction;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.math.BigDecimal;

@UtilityClass
public class InteractionMapper {

	public Interaction toEntity(@NonNull UserActionAvro avro) {
		return Interaction.builder()
				.userId(avro.getUserId())
				.eventId(avro.getEventId())
				.actionWeight(toActionWeight(avro.getActionType()))
				.build();
	}

	@NonNull
	private BigDecimal toActionWeight(@NonNull ActionTypeAvro avroType) {
		return switch (avroType) {
			case VIEWS -> BigDecimal.valueOf(0.4);
			case REGISTER -> BigDecimal.valueOf(0.8);
			case LIKE -> BigDecimal.ONE;
		};
	}

}

package ru.practicum.ewm.stats.analyzer.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.analyzer.entity.Similarity;
import ru.practicum.ewm.stats.avro.EventsSimilarityAvro;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class SimilarityMapper {

	public Similarity toEntity(@NonNull EventsSimilarityAvro avro) {
		return Similarity.builder()
				.eventA(avro.getEventA())
				.eventB(avro.getEventB())
				.score(toBigDecimal(avro.getScore()))
				.build();
	}

	@NonNull
	private BigDecimal toBigDecimal(double value) {
		return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
	}

}

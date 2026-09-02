package ru.practicum.aggregation.model.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Builder
public class StatsRequestData {

	private List<String> uris;

	@Builder.Default
	private LocalDateTime start = LocalDateTime.parse("1970-01-01T00:00:00");

	@Builder.Default
	private LocalDateTime end = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

	@Builder.Default
	private Boolean unique = false;
}
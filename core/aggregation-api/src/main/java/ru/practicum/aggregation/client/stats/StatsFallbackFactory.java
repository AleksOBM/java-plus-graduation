package ru.practicum.aggregation.client.stats;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.error.exception.unavailable.StatsServerUnavailableException;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatsFallbackFactory implements FallbackFactory<StatsClient> {

	@Override
	public StatsClient create(Throwable cause) {

		return new StatsClient() {

			@Override
			public void hit(EndpointHitDto endpointHitDto) {
				throw new StatsServerUnavailableException("", cause);
			}

			@Override
			public List<ViewStatsDto> getStats(LocalDateTime start,
			                                   LocalDateTime end,
			                                   List<String> uris,
			                                   boolean unique) {

				throw new StatsServerUnavailableException("", cause);
			}
		};
	}

}

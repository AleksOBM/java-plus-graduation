package ru.practicum.ewm.events.statistic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.error.exception.stats.HitRequestException;
import ru.practicum.aggregation.error.exception.stats.StatsResponseException;
import ru.practicum.stat.client.StatClient;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.StatsRequest;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatRestRepository {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final LocalDateTime startUnixEpoch = LocalDateTime.parse("1970-01-01T00:00:00");

	@Value("${spring.application.name}")
	private String appName;

	private final StatClient statClient;

	public void sendHitRequest(HttpServletRequest request) {
		try {
			statClient.hit(EndpointHitDto.builder()
					.ip(request.getRemoteAddr())
					.uri(request.getRequestURI())
					.app(appName)
					.timestamp(LocalDateTime.now())
					.build()
			);
		} catch (Exception ex) {
			throw new HitRequestException(ex);
		}
	}

	public List<ViewStatsDto> getStat(List<String> statUris,
	                                  LocalDateTime rangeStart,
	                                  LocalDateTime rangeEnd,
	                                  boolean uniqe) {
		if (statUris == null || statUris.isEmpty()) {
			throw new StatsResponseException();
		}

		if (rangeStart == null) {
			rangeStart = startUnixEpoch;
		}

		if (rangeEnd == null) {
			rangeEnd = LocalDateTime.now();
		}

		StatsRequest request = StatsRequest.builder()
				.uris(statUris)
				.start(rangeStart.truncatedTo(ChronoUnit.MILLIS).format(formatter))
				.end(rangeEnd.truncatedTo(ChronoUnit.MILLIS).format(formatter))
				.unique(uniqe)
				.build();

		List<ViewStatsDto> stats;
		try {
			stats = statClient.getStat(request);
		} catch (Exception ex) {
			throw new StatsResponseException(ex);
		}

		if (stats == null) {
			throw new StatsResponseException();
		}

		return stats;
	}

	public List<ViewStatsDto> getStat(List<String> statUris, boolean uniqe) {
		return getStat(statUris, null, null, uniqe);
	}

}

package ru.practicum.aggregation.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteCallExecutor;
import ru.practicum.aggregation.client.RemoteCallResult;
import ru.practicum.aggregation.client.stats.StatsClient;
import ru.practicum.aggregation.error.exception.stats.StatsResponseException;
import ru.practicum.aggregation.error.exception.unavailable.StatsServerUnavailableException;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsFeignRepositoryImpl implements StatsFeignRepository {

	Environment environment;
	StatsClient statsClient;

	@Override
	public void sendHitRequest(@NonNull HttpServletRequest request) {
		hit(EndpointHitDto.builder()
				.ip(request.getRemoteAddr())
				.uri(request.getRequestURI())
				.app(environment.getProperty("spring.application.name"))
				.timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
				.build());
	}

	@Override
	public List<ViewStatsDto> getStat(List<String> statUris,
	                                  LocalDateTime rangeStart,
	                                  LocalDateTime rangeEnd,
	                                  boolean uniqe) {

		if (statUris == null || statUris.isEmpty()) {
			throw new StatsResponseException();
		}

		if (rangeStart == null) {
			rangeStart = startUnixEpoch.truncatedTo(ChronoUnit.SECONDS);
		}

		if (rangeEnd == null) {
			rangeEnd = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		}

		return getStatistic(rangeStart, rangeEnd, statUris, uniqe);
	}

	@Override
	public List<ViewStatsDto> getStat(List<String> statUris, boolean uniqe) {
		return getStat(statUris, null, null, uniqe);
	}

	@SuppressWarnings("unused")
	private void hit(EndpointHitDto endpointHitDto) {
		log.info("""
				PLEASE WAITING
				Система регистрирует статистику
				{}""", endpointHitDto);
		switch (RemoteCallExecutor.executeVoid(() -> statsClient.hit(endpointHitDto))) {

			case RemoteCallResult.Success(var nullable) -> {
			}
			case RemoteCallResult.Failure(var exeptionNullable) -> {
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось зарегистрировать статистику
						{}""", endpointHitDto);
				throw new StatsServerUnavailableException("", cause);
			}
		}
	}

	private List<ViewStatsDto> getStatistic(LocalDateTime start,
	                                        LocalDateTime end,
	                                        List<String> uris,
	                                        boolean unique) {
		log.info("""
				PLEASE WAITING
				Система получает статистику
				uris: {}""", uris);
		return switch (RemoteCallExecutor.execute(() -> statsClient.getStats(start, end, uris, unique))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var exeptionNullable) -> throw exeptionNullable;
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить статистику
						uris: {}""", uris);
				throw new StatsServerUnavailableException("", cause);
			}
		};
	}
}

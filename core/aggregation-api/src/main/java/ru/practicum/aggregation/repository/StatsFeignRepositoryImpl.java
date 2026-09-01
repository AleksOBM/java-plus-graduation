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
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.aggregation.model.data.StatsRequestData;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
	public Optional<List<ViewStatsDto>> getStatList(@NonNull StatsRequestData request) {

		var statUris = request.getUris();
		if (statUris.isEmpty()) {
			throw new StatsResponseException();
		}

		return getStatistic(request.getStart(), request.getEnd(), statUris, request.getUnique());
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
			case RemoteCallResult.Degraded(var cause) -> log.warn("""
					Деградация
					Не удалось зарегистрировать статистику
					{}""", endpointHitDto, cause);

		}
	}

	@NonNull
	private Optional<List<ViewStatsDto>> getStatistic(LocalDateTime start,
	                                                  LocalDateTime end,
	                                                  List<String> uris,
	                                                  boolean unique) {
		log.info("""
				PLEASE WAITING
				Система получает статистику
				uris: {}""", uris);
		return switch (RemoteCallExecutor.execute(() -> statsClient.getStats(start, end, uris, unique))) {
			case RemoteCallResult.Success(var response) -> Optional.of(response);
			case RemoteCallResult.Failure(var exeptionNullable) -> throw exeptionNullable;
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить статистику
						uris: {}""", uris, cause);

				yield Optional.empty();
			}
		};
	}
}

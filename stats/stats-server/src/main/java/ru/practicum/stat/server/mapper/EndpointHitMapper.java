package ru.practicum.stat.server.mapper;

import org.springframework.lang.NonNull;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.server.entity.EndpointHit;

import java.time.temporal.ChronoUnit;

public class EndpointHitMapper {

	public static EndpointHit toEntity(@NonNull EndpointHitDto dto) {
		return EndpointHit.builder()
				.app(dto.getApp())
				.uri(dto.getUri())
				.ip(dto.getIp())
				.timestamp(dto.getTimestamp().truncatedTo(ChronoUnit.SECONDS))
				.build();
	}
}

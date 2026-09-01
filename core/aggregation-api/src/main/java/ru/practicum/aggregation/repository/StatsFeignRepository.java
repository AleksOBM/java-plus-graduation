package ru.practicum.aggregation.repository;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsFeignRepository {

	LocalDateTime startUnixEpoch = LocalDateTime.parse("1970-01-01T00:00:00");

	void sendHitRequest(HttpServletRequest request);

	List<ViewStatsDto> getStat(List<String> statUris,
	                           LocalDateTime rangeStart,
	                           LocalDateTime rangeEnd,
	                           boolean uniqe);

	List<ViewStatsDto> getStat(List<String> statUris, boolean uniqe);
}

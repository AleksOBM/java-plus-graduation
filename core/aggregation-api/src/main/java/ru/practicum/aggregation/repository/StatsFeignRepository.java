package ru.practicum.aggregation.repository;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.aggregation.model.data.StatsRequestData;
import ru.practicum.stat.dto.ViewStatsDto;

import java.util.List;
import java.util.Optional;

public interface StatsFeignRepository {

	void sendHitRequest(HttpServletRequest request);

	Optional<List<ViewStatsDto>> getStatList(StatsRequestData request);

}

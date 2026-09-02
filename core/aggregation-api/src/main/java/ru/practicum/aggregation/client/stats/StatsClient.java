package ru.practicum.aggregation.client.stats;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(
		name = "stats-server",
		fallbackFactory = StatsFallbackFactory.class
)
public interface StatsClient {

	@PostMapping("/hit")
	void hit(@RequestBody EndpointHitDto endpointHitDto);

	@GetMapping("/stats")
	List<ViewStatsDto> getStats(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
			@RequestParam List<String> uris,
			@RequestParam boolean unique);

}

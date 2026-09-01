package ru.practicum.ewm.events.controller.free;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.compilation.output.CompilationDto;
import ru.practicum.aggregation.dto.compilation.come.get.CompilationSearchFilter;
import ru.practicum.ewm.events.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class FreeCompilationController {

	private final CompilationService compilationService;

	/**
	 * В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список
	 */
	@GetMapping
	public List<CompilationDto> getCompilations(
			@RequestParam(required = false) Boolean pinned,
			@RequestParam(defaultValue = "0") Integer from,
			@RequestParam(defaultValue = "10") Integer size,
			@NonNull HttpServletRequest request
	) {
		log.info("""
				ENDPOINT
				Получение подборок событий
				{} {}""", request.getMethod(), request.getRequestURI());

		CompilationSearchFilter filter = CompilationSearchFilter.builder()
				.pinned(pinned)
				.from(from)
				.size(size)
				.build();

		return compilationService.getByFilter(filter, request);
	}

	@GetMapping("/{compId}")
	public CompilationDto getCompilationById(@PathVariable Long compId,
	                                         @NonNull HttpServletRequest request) {

		log.info("""
				ENDPOINT
				Получение подборки событий по её id
				{} {}""", request.getMethod(), request.getRequestURI());

		return compilationService.getById(compId, request);
	}

}

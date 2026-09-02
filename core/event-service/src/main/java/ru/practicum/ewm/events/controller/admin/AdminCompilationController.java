package ru.practicum.ewm.events.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.compilation.output.CompilationDto;
import ru.practicum.aggregation.dto.compilation.come.update.CompilationUpdateDto;
import ru.practicum.aggregation.dto.compilation.come.create.NewCompilationDto;
import ru.practicum.ewm.events.service.compilation.CompilationService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

	private final CompilationService compilationService;

	/**
	 * подборка может не содержать событий
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto,
	                                        @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Добавление новой подборки
				{} {}""", request.getMethod(), request.getRequestURI());

		return compilationService.addCompilation(newCompilationDto);
	}

	@PatchMapping("/{compId}")
	public CompilationDto updateCompilation(@RequestBody @Valid CompilationUpdateDto compilationUpdateDto,
	                                        @PathVariable Long compId,
	                                        @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Обновление подборки
				{} {}""", request.getMethod(), request.getRequestURI());

		return compilationService.updateCompilation(compId, compilationUpdateDto);
	}

	@DeleteMapping("/{compId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delById(@PathVariable Long compId,
	                    @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Удаление подборки
				{} {}""", request.getMethod(), request.getRequestURI());

		compilationService.delById(compId);
	}
}

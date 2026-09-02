package ru.practicum.ewm.events.controller.free;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.category.output.CategoryDto;
import ru.practicum.ewm.events.service.category.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class FreeCategoryController {

	private final CategoryService categoryService;

	/**
	 * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
	 *
	 * @param from количество категорий, которые нужно пропустить для формирования текущего набора
	 *             Default value : 0
	 * @param size количество категорий в наборе
	 *             Default value : 10
	 */
	@GetMapping
	public List<CategoryDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
	                                 @RequestParam(defaultValue = "10") @Positive Integer size,
	                                 @NonNull HttpServletRequest request) {

		log.info("""
				ENDPOINT
				Получение категорий
				{} {}""", request.getMethod(), request.getRequestURI());

		return categoryService.findAll(from, size);
	}

	/**
	 * В случае, если категории с заданным id не найдено, возвращает статус код 404
	 */
	@GetMapping("/{catId}")
	public CategoryDto findById(@PathVariable @Positive Long catId,
	                            @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Получение информации о категории по её идентификатору
				{} {}""", request.getMethod(), request.getRequestURI());

		return categoryService.findById(catId);
	}

}

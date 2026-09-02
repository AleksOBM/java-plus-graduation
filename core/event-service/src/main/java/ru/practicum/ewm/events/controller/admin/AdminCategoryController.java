package ru.practicum.ewm.events.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.category.output.CategoryDto;
import ru.practicum.aggregation.dto.category.come.NewCategoryDto;
import ru.practicum.ewm.events.service.category.CategoryService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

	private final CategoryService categoryService;

	/**
	 * Обратите внимание: имя категории должно быть уникальным
	 * @param newCategoryDto данные добавляемой категории
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryDto addNewCategory(@RequestBody @Valid NewCategoryDto newCategoryDto,
	                                  @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Добавление новой категории
				{} {}""", request.getMethod(), request.getRequestURI());

		return categoryService.adminAddNewCategory(newCategoryDto);
	}

	/**
	 * Обратите внимание: имя категории должно быть уникальным
	 */
	@PatchMapping("/{catId}")
	public CategoryDto updateCategory(@PathVariable Long catId,
	                                  @RequestBody @Valid CategoryDto categoryDto,
	                                  @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Изменение категории
				{} {}""", request.getMethod(), request.getRequestURI());

		return categoryService.updateCategory(catId, categoryDto);
	}

	/**
	 * Обратите внимание: с категорией не должно быть связано ни одного события.
	 */
	@DeleteMapping("/{catId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable Long catId,
	                           @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Удаление категории
				{} {}""", request.getMethod(), request.getRequestURI());

		categoryService.deleteCategory(catId);
	}
}

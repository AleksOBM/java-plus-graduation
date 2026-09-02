package ru.practicum.ewm.events.service.category;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.category.output.CategoryDto;
import ru.practicum.aggregation.dto.category.come.NewCategoryDto;

import java.util.List;

@Transactional
public interface CategoryService {

	CategoryDto adminAddNewCategory(NewCategoryDto newCategoryDto);

	@Transactional(readOnly = true)
	List<CategoryDto> findAll(Integer from, Integer size);

	@Transactional(readOnly = true)
	CategoryDto findById(Long catId);

	CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

	void deleteCategory(Long catId);
}

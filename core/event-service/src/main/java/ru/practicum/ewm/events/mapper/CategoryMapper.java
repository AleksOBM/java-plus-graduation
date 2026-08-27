package ru.practicum.ewm.events.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.category.output.CategoryDto;
import ru.practicum.aggregation.dto.category.come.NewCategoryDto;
import ru.practicum.ewm.events.entity.Category;

@UtilityClass
public class CategoryMapper {
	public CategoryDto toDto(@NonNull Category category) {
		return CategoryDto.builder()
				.id(category.getId())
				.name(category.getName())
				.build();
	}

	public Category toEntity(@NonNull NewCategoryDto newCategoryDto) {
		return Category.builder()
				.name(newCategoryDto.name())
				.build();
	}
}

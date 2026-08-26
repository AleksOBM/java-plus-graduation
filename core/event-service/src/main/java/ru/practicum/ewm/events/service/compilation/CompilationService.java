package ru.practicum.ewm.events.service.compilation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.compilation.CompilationDto;
import ru.practicum.aggregation.dto.compilation.CompilationSearchFilter;
import ru.practicum.aggregation.dto.compilation.CompilationUpdateDto;
import ru.practicum.aggregation.dto.compilation.NewCompilationDto;

import java.util.List;

@Transactional
public interface CompilationService {

	@Transactional(readOnly = true)
	CompilationDto getById(Long compilationId, HttpServletRequest request);

	void delById(Long compilationId);

	CompilationDto addCompilation(NewCompilationDto compilation);

	CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilation);

	@Transactional(readOnly = true)
	List<CompilationDto> getByFilter(CompilationSearchFilter filter, HttpServletRequest request);
}

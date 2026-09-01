package ru.practicum.aggregation.dto.compilation.come.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * данные новой подборки
 * @apiNote
 * {@link Set}<code><</code>{@link Long}<code>></code> events<br/>
 * <code>boolean</code> pinned<br/>
 * {@link String} title
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

	private Set<Long> events;

	@Builder.Default
	private boolean pinned = false;

	@NotBlank
	@Size(min = 1, max = 50)
	private String title;
}
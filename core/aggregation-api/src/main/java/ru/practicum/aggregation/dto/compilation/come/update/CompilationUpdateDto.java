package ru.practicum.aggregation.dto.compilation.come.update;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @apiNote
 * {@link Set}<code><</code>{@link Long}<code>></code> events<br/>
 * {@link Boolean} pinned<br/>
 * {@link String} title
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {

	private Set<Long> events;

	private Boolean pinned;

	@Size(min = 1, max = 50)
	private String title;
}

package ru.practicum.aggregation.dto.compilation.come.get;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link Boolean} pinned - искать только закрепленные/не закрепленные подборки<br/>
 * {@link Integer} from - количество элементов, которые нужно пропустить для формирования текущего набора<br/>
 *                Default value : 0<br/>
 * {@link Integer} size - количество элементов в наборе<br/>
 *                Default value : 10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationSearchFilter {

	Boolean pinned;

	@Builder.Default
	Integer from = 0;

	@Builder.Default
	Integer size = 10;
}

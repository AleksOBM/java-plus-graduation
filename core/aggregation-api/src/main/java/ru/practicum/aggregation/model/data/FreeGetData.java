package ru.practicum.aggregation.model.data;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @param text          текст для поиска в содержимом аннотации и подробном описании события
 * @param categories    список идентификаторов категорий в которых будет вестись поиск
 * @param paid          поиск только платных/бесплатных событий
 * @param rangeStart    дата и время не раньше которых должно произойти событие
 * @param rangeEnd      дата и время не позже которых должно произойти событие
 * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие<br/>
 *                      Default value : false
 * @param sort          Вариант сортировки: по дате события или по количеству просмотров<br/>
 *                      Available values : EVENT_DATE, VIEWS
 * @param from          количество событий, которые нужно пропустить для формирования текущего набора<br/>
 *                      Default value : 0
 * @param size          количество событий в наборе<br/>
 *                      Default value : 10
 */
@NotNull
@Builder
public record FreeGetData(
		String text,
		List<Integer> categories,
		Boolean paid,
		LocalDateTime rangeStart,
		LocalDateTime rangeEnd,
		Boolean onlyAvailable,
		FreeEventSort sort,
		Integer from,
		Integer size
) {

	public enum FreeEventSort {
		EVENT_DATE, VIEWS
	}
}

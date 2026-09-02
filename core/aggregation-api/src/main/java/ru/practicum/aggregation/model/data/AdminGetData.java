package ru.practicum.aggregation.model.data;

import lombok.Builder;
import ru.practicum.aggregation.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @param users      список id пользователей, чьи события нужно найти
 * @param states     список состояний в которых находятся искомые события
 * @param categories список id категорий в которых будет вестись поиск
 * @param rangeStart дата и время не раньше которых должно произойти событие
 * @param rangeEnd   дата и время не позже которых должно произойти событие
 * @param from       количество событий, которые нужно пропустить для формирования текущего набора</br>
 *                   Default value : 0
 * @param size       количество событий в наборе</br>
 *                   Default value : 10
 */
@Builder
public record AdminGetData(
		List<Integer> users,
		List<EventState> states,
		List<Integer> categories,
		LocalDateTime rangeStart,
		LocalDateTime rangeEnd,
		Integer from,
		Integer size
) {
}
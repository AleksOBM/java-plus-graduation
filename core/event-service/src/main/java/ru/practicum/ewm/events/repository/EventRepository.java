package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.ewm.events.entity.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

	List<Event> findByInitiatorId(Long userId, PageRequest pageRequest);

	boolean existsByCategoryId(Long categoryId);

	boolean existsByIdAndState(Long eventId, EventState state);
}

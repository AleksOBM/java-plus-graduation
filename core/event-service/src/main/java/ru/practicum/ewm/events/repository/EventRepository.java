package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.ewm.events.entity.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

	List<Event> findByInitiatorId(Long userId, PageRequest pageRequest);

	@Query("""
			select e.initiatorId from Event e
			where e.id = :eventId
			""")
	Long getInitiatorIdByEventId(Long eventId);

	boolean existsByCategoryId(Long categoryId);

	boolean existsByIdAndState(Long eventId, EventState state);
}

package ru.practicum.ewm.stats.analyzer.repository;

import org.hibernate.query.spi.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.analyzer.entity.Interaction;
import ru.practicum.ewm.stats.analyzer.model.EventInteraction;
import ru.practicum.ewm.stats.analyzer.model.EventWeightSum;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface InteractionsRepository extends JpaRepository<Interaction, Long> {

	@Query("""
			select new ru.practicum.ewm.stats.analyzer.model.EventWeightSum(
						ir.eventId,
						coalesce(sum(ir.actionWeight), 0)
			)
			from Interaction ir
			where ir.eventId in :eventIds
			group by ir.eventId
			""")
	Stream<EventWeightSum> aggregateActionWeightsByEvent(Collection<Long> eventIds);

	@Query("""
			select new ru.practicum.ewm.stats.analyzer.model.EventInteraction(
						ir.userId,
						ir.eventId,
						ir.actionWeight
			)
			from Interaction ir
			where ir.eventId in :eventIds
			and ir.userId = :userId
			""")
	Set<EventInteraction> aggregateActionWeightByEventAndByUser(Collection<Long> eventIds, Long userId);

	@Query("""
			    SELECT DISTINCT i.eventId FROM Interaction i
			    WHERE i.userId = :userId
			""")
	Set<Long> findInteractedEventIds(long userId);

	Optional<Interaction> findByUserIdAndEventId(long userId, long eventId);

	@Query("""
			select ir.eventId
			from Interaction ir
			where ir.userId = :userId
			order by ir.id desc
			""")
	List<Long> findRecentlyInteractions(long userId, Limit limit);
}

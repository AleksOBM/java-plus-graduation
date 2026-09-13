package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.analyzer.entity.Similarity;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {

	Optional<Similarity> findByEventAAndEventB(Long eventA, Long eventB);

	@Query("""
			SELECT new ru.practicum.ewm.stats.analyzer.model.EventSimilarity(
			            s.eventA, s.eventB, s.score
			)
			FROM Similarity s
			WHERE s.eventA = :eventId OR s.eventB = :eventId
			""")
	List<EventSimilarity> findSimilaritiesByEventId(long eventId);

	@Query("""
			SELECT DISTINCT new ru.practicum.ewm.stats.analyzer.model.EventSimilarity(
			            s.eventA, s.eventB, s.score
			)
			FROM Similarity s
			WHERE s.eventA IN :eventIds OR s.eventB IN :eventIds
			""")
	List<EventSimilarity> findSimilaritiesByManyEventIds(List<Long> eventIds);
}

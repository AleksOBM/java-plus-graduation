package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.entity.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

	Page<Compilation> findAllByPinned(boolean pinned, Pageable pageable);
}

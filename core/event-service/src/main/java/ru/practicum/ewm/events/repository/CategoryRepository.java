package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	boolean existsByName(String name);
}

package ru.practicum.ewm.stats.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "similariries", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"eventA", "eventB"})
})
public class Similarity extends BaseEntity {

	@Column(name = "event_a", nullable = false)
	Long eventA;

	@Column(name = "event_b", nullable = false)
	Long eventB;

	@Column(nullable = false, precision = 3, scale = 2)
	BigDecimal score;
}

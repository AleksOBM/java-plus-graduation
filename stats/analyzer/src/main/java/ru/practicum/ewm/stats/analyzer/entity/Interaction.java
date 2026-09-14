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
@Table(name = "interactions", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"userId", "eventId"})
})
public class Interaction extends BaseEntity {

	@Column(nullable = false)
	Long userId;

	@Column(nullable = false)
	Long eventId;

	@Column(nullable = false, precision = 3, scale = 2)
	BigDecimal actionWeight;
}

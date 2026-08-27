package ru.practicum.ewm.ratings.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.aggregation.enums.Reaction;
import ru.practicum.aggregation.model.entity.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "ratings", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "event_id"})
})
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rating extends BaseEntity {

	@JoinColumn(name = "user_id", nullable = false)
	Long userId;

	@JoinColumn(name = "event_id", nullable = false)
	Long eventId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Reaction reaction;
}

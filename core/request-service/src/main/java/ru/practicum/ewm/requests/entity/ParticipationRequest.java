package ru.practicum.ewm.requests.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.aggregation.enums.ParticipationStatus;
import ru.practicum.aggregation.model.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest extends BaseEntity {

	LocalDateTime created;

	@JoinColumn(name = "event_id", nullable = false)
	Long eventId;

	@JoinColumn(name = "requester_id", nullable = false)
	Long requesterId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	ParticipationStatus status;
}

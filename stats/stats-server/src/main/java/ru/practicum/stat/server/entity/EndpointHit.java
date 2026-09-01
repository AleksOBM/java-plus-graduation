package ru.practicum.stat.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String app;

	@Column(nullable = false, length = 512)
	private String uri;

	@Column(nullable = false, length = 45)
	private String ip;

	@Column(name = "hit_timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Override
	public String toString() {
		return """
				{
					"id": "%s",
					"app": "%s",
					"uri": "%s",
					"ip": "%s",
					"timestamp": "%s"
				}""".formatted(id, app, uri, ip, timestamp);
	}
}

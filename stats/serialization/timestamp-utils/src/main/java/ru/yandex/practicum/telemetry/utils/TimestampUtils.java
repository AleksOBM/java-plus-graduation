package ru.yandex.practicum.telemetry.utils;

import com.google.protobuf.Timestamp;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@UtilityClass
@SuppressWarnings("unused")
public class TimestampUtils {

	private final ZoneId zoneId = ZoneId.systemDefault();

	public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	public Timestamp toTimestamp(@NonNull Instant instant) {
		return Timestamp.newBuilder()
				.setSeconds(instant.getEpochSecond())
				.setNanos(instant.getNano())
				.build();
	}

	public Timestamp toTimestamp(@NonNull LocalDateTime localDateTime) {
		return toTimestamp(localDateTime.atZone(zoneId).toInstant());
	}

	public Instant toInstant(@NonNull Timestamp timestamp) {
		return Instant.ofEpochSecond(
				timestamp.getSeconds(),
				timestamp.getNanos()
		);
	}

	public Instant toInstant(@NonNull LocalDateTime localDateTime) {
		return toInstant(toTimestamp(localDateTime));
	}

	public LocalDateTime toLocalDateTime(@NonNull Timestamp timestamp) {
		return LocalDateTime.ofInstant(TimestampUtils.toInstant(timestamp), zoneId)
				.truncatedTo(ChronoUnit.MILLIS);
	}

	public LocalDateTime toLocalDateTime(long timestamp) {
		return toLocalDateTime(TimestampUtils.toTimestamp(Instant.ofEpochSecond(timestamp)))
				.truncatedTo(ChronoUnit.MILLIS);
	}

	public LocalDateTime toLocalDateTime(@NonNull Instant instant) {
		return toLocalDateTime(TimestampUtils.toTimestamp(instant));
	}

	public String toString(@NonNull Timestamp timestamp) {
		return formatter.format(toLocalDateTime(timestamp));
	}

	public String toString(long longTimestamp) {
		return toString(toTimestamp(Instant.ofEpochSecond(longTimestamp).atZone(zoneId).toInstant()));
	}
}

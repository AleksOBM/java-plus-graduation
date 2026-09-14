package ru.practicum.ewm.stats.collector.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

@UtilityClass
public class UserActionMapper {

	public UserActionAvro toAvro(@NonNull UserActionProto proto) {
		return UserActionAvro.newBuilder()
				.setUserId(proto.getUserId())
				.setEventId(proto.getEventId())
				.setActionType(UserActionMapper.toAvroActionType(proto.getActionType()))
				.setTimestamp(TimestampUtils.toInstant(proto.getTimestamp()))
				.build();
	}

	private ActionTypeAvro toAvroActionType(@NonNull ActionTypeProto protoType) {
		return switch (protoType) {
			case ACTION_VIEW -> ActionTypeAvro.VIEWS;
			case ACTION_LIKE -> ActionTypeAvro.LIKE;
			case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
			default -> throw new IllegalArgumentException("Unknown action type: " + protoType);
		};
	}

}

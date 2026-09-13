package ru.practicum.stats.client.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.stats.client.dto.ActionType;

@UtilityClass
public class ActionTypeMapper {

	public ActionType toActionType(@NonNull ActionTypeProto proto) {
		return switch (proto) {
			case ACTION_VIEW -> ActionType.VIEWS;
			case ACTION_REGISTER -> ActionType.REGISTER;
			case ACTION_LIKE -> ActionType.LIKE;
			case UNRECOGNIZED -> throw new IllegalArgumentException("Action type is UNRECOGNIZED");
		};
	}

	public ActionTypeProto toProto(@NonNull ActionType actionType) {
		return switch (actionType) {
			case VIEWS -> ActionTypeProto.ACTION_VIEW;
			case REGISTER -> ActionTypeProto.ACTION_REGISTER;
			case LIKE -> ActionTypeProto.ACTION_LIKE;
		};
	}
}

package ru.practicum.ewm.events.mapper;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.aggregation.enums.UserStateAction;

@Component
public class StateMapper {

	@Nullable
	public static EventState mapUserEventAction(UserStateAction action) {
		return switch (action) {
			case CANCEL_REVIEW -> EventState.CANCELED;
			case SEND_TO_REVIEW -> EventState.PENDING;
			case null, default -> null;
		};
	}

	@Nullable
	public static EventState mapAdminEventAction(UserStateAction action) {
		return switch (action) {
			case PUBLISH_EVENT -> EventState.PUBLISHED;
			case REJECT_EVENT -> EventState.CANCELED;
			case null, default -> null;
		};
	}

}

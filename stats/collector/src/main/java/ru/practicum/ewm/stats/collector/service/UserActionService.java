package ru.practicum.ewm.stats.collector.service;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface UserActionService {

	void collectUserAction(UserActionProto userAction);
}

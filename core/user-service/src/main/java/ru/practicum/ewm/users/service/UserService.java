package ru.practicum.ewm.users.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.aggregation.dto.user.come.NewUserRequest;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

	@Transactional
	UserDto createUser(NewUserRequest newUserRequest);

	UserDto getUser(long userId);

	void remoteCheckUser(long userId);

	List<UserShortDto> getShortUsersByIds(List<Long> userIds);

	List<UserDto> findUsers(List<Long> ids, int from, int size);

	@Transactional
	void deleteUser(long userId);
}

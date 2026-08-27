package ru.practicum.aggregation.repository;

import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

import java.util.List;

public interface UserFeignRepository {

	UserDto getUserDtoById(long userId);

	List<UserShortDto> getUsersByIds(List<Long> userIds);

	void checkUser(Long userId);

}

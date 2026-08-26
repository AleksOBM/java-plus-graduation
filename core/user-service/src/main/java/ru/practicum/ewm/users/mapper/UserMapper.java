package ru.practicum.ewm.users.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.user.NewUserRequest;
import ru.practicum.aggregation.dto.user.UserDto;
import ru.practicum.aggregation.dto.user.UserShortDto;
import ru.practicum.ewm.users.entity.User;

@UtilityClass
public class UserMapper {

	public UserShortDto toUserShortDto(@NonNull User user) {
		return UserShortDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}

	public UserDto toUserDto(@NonNull User user) {
		return UserDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}

	public User toEntity(@NonNull NewUserRequest newUserRequest) {
		return User.builder()
				.name(newUserRequest.name())
				.email(newUserRequest.email())
				.build();
	}

}
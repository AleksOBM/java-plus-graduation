package ru.practicum.ewm.users.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.user.come.NewUserRequest;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.ewm.users.entity.User;

@UtilityClass
public class UserMapper {

	public UserShortDto toUserShortDto(@NonNull User user) {
		return UserShortDto.builder()
				.id(user.getId())
				.name(user.getName())
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
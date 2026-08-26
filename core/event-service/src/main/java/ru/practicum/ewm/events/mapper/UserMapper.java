package ru.practicum.ewm.events.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.user.UserDto;
import ru.practicum.aggregation.dto.user.UserShortDto;
import ru.practicum.ewm.events.model.User;

@UtilityClass
public class UserMapper {

	public UserShortDto toUserShortDto(@NonNull User user) {
		return UserShortDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}

	public User toEntity(@NonNull UserDto userDto) {
		return User.builder()
				.id(userDto.id())
				.name(userDto.name())
				.email(userDto.email())
				.build();
	}

}
package ru.practicum.ewm.requests.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.user.UserDto;
import ru.practicum.ewm.requests.model.User;

@UtilityClass
public class UserMapper {

	public User toEntity(@NonNull UserDto userDto) {
		return User.builder()
				.id(userDto.id())
				.name(userDto.name())
				.email(userDto.email())
				.build();
	}

}
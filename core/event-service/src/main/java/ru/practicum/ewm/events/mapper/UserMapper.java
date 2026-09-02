package ru.practicum.ewm.events.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

@UtilityClass
public class UserMapper {

	public UserShortDto toUserShortDto(@NonNull UserDto userDto) {
		return UserShortDto.builder()
				.id(userDto.id())
				.name(userDto.name())
				.build();
	}

}
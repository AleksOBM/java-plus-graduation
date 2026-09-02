package ru.practicum.aggregation.client.user;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteExceptionMapper;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

import java.util.List;

@Component
public class UserFallbackFactory implements FallbackFactory<UserClient> {

	@Override
	public UserClient create(Throwable cause) {

		return new UserClient() {

			@Override
			public UserDto getUser(Long userId) {
				throw RemoteExceptionMapper.mapUserException(cause);
			}

			@Override
			public void checkUser(Long userId) {
				throw RemoteExceptionMapper.mapUserException(cause);
			}

			@Override
			public List<UserShortDto> getShortUsersByIds(List<Long> userIds) {
				throw RemoteExceptionMapper.mapUserException(cause);
			}
		};
	}

}

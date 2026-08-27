package ru.practicum.aggregation.repository;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.aggregation.client.RemoteCallExecutor;
import ru.practicum.aggregation.client.RemoteCallResult;
import ru.practicum.aggregation.client.user.UserClient;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.aggregation.error.exception.processing.UserProcessingException;
import ru.practicum.aggregation.error.exception.unavailable.UserServiceUnavailableException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFeignRepositoryImpl implements UserFeignRepository {

	UserClient userClient;

	@Override
	public @NonNull UserDto getUserDtoById(long userId) {
		return switch (RemoteCallExecutor.execute(() -> userClient.getUser(userId))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var message) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить пользователя с id={}
						""", userId);
				throw new UserProcessingException(message);
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить пользователя с id={}
						""", userId);
				throw new UserServiceUnavailableException(cause);
			}
		};
	}

	@Override
	public List<UserShortDto> getUsersByIds(List<Long> userIds) {
		return switch (RemoteCallExecutor.execute(() -> userClient.getShortUsersByIds(userIds))) {
			case RemoteCallResult.Success(var response) -> response;
			case RemoteCallResult.Failure(var message) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось получить краткий список пользователей
						userIds: {}
						""", userIds);
				throw new UserProcessingException(message);
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось получить краткий список пользователей
						userIds: {}
						""", userIds);
				throw new UserServiceUnavailableException(cause);
			}
		};
	}

	@SuppressWarnings("unused")
	@Override
	public void checkUser(Long userId) {
		switch (RemoteCallExecutor.executeVoid(() -> userClient.checkUser(userId))) {
			case RemoteCallResult.Success(var nullable) -> {
			}
			case RemoteCallResult.Failure(var message) -> {
				log.warn("""
						Бизнес-исключение
						Не удалось проверить наличие пользователя с id={}
						""", userId);
				throw new UserProcessingException(message);
			}
			case RemoteCallResult.Degraded(var cause) -> {
				log.warn("""
						Деградация
						Не удалось проверить наличие пользователя с id={}
						""", userId);
				throw new UserServiceUnavailableException(cause);
			}
		}
	}

}

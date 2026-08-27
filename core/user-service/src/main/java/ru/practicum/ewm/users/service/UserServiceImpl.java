package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.user.come.NewUserRequest;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.aggregation.error.exception.ConflictException;
import ru.practicum.aggregation.error.exception.NotFoundException;
import ru.practicum.ewm.users.entity.User;
import ru.practicum.ewm.users.mapper.UserMapper;
import ru.practicum.ewm.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto createUser(@NonNull NewUserRequest newUserRequest) {
		if (userRepository.existsByEmail(newUserRequest.email())) {
			throw new ConflictException(
					"Пользователь с такой почтой " + newUserRequest.email() + " уже существует");
		}
		User user = UserMapper.toEntity(newUserRequest);
		return UserMapper.toUserDto(userRepository.save(user));
	}

	@Override
	public UserDto getUser(long userId) {
		return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() ->
						new NotFoundException("Пользователь с id=%s не найден".formatted(userId))
				)
		);
	}

	@Override
	public void remoteCheckUser(long userId) {
		checkUser(userId);
	}

	@Override
	public List<UserShortDto> getShortUsersByIds(List<Long> userIds) {
		return userRepository.findAllById(userIds).stream()
				.map(UserMapper::toUserShortDto)
				.toList();
	}

	@Override
	public List<UserDto> findUsers(List<Long> ids, int from, int size) {
		PageRequest page = PageRequest.of(from / size, size);
		List<User> users;
		if (ids == null || ids.isEmpty()) {
			users = userRepository.findAll(page).getContent();
		} else {
			users = userRepository.findAllByIdIn(ids, page);
		}
		return users.stream()
				.map(UserMapper::toUserDto)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteUser(long userId) {
		checkUser(userId);
		userRepository.deleteById(userId);
	}

	private void checkUser(long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id=%s не найден".formatted(userId));
		}
	}

}

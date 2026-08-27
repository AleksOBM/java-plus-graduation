package ru.practicum.ewm.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.user.come.NewUserRequest;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.ewm.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final UserService userService;

	/**
	 * Добавление нового пользователя
	 *
	 * @param newUserRequest Данные добавляемого пользователя
	 * @return {@link UserDto}
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
		return userService.createUser(newUserRequest);
	}

	/**
	 * Получение пользователя по ID
	 *
	 * @param userId ID пользователя
	 * @return {@link UserDto}
	 */
	@GetMapping("/{userId}")
	public UserDto getUser(@PathVariable Long userId) {
		return userService.getUser(userId);
	}

	/**
	 * Проверка наличия пользователя по ID
	 *
	 * @param userId ID пользователя
	 */
	@GetMapping("/{userId}/check")
	public void checkUser(@PathVariable Long userId) {
		userService.remoteCheckUser(userId);
	}

	/**
	 * Получение краткого списка пользователей
	 *
	 * @param userIds id пользователей
	 * @return {@link UserShortDto}
	 */
	@GetMapping("/short")
	public List<UserShortDto> getShortUsersByIds(@RequestParam List<Long> userIds) {
		return userService.getShortUsersByIds(userIds);
	}

	/**
	 * Получение информации о пользователях
	 * <p>
	 * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
	 * либо о конкретных (учитываются указанные идентификаторы)
	 * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список
	 *
	 * @param ids  id пользователей
	 * @param from количество элементов, которые нужно пропустить для формирования текущего набора
	 *             Default value : 0
	 * @param size количество элементов в наборе
	 *             Default value : 10
	 * @return List<{@link UserDto}>
	 */
	@GetMapping
	public List<UserDto> findUsers(
			@RequestParam(required = false) List<Long> ids,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size
	) {
		return userService.findUsers(ids, from, size);
	}

	/**
	 * Удаление пользователя
	 *
	 * @param userId id пользователя
	 */
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
	}

}

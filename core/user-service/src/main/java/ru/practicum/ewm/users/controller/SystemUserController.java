package ru.practicum.ewm.users.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;
import ru.practicum.ewm.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/system/users")
@RequiredArgsConstructor
public class SystemUserController {

	private final UserService userService;

	/**
	 * Проверка наличия пользователя по ID
	 *
	 * @param userId ID пользователя
	 */
	@GetMapping("/{userId}/check")
	public void checkUser(@PathVariable @Positive Long userId) {
		userService.remoteCheckUser(userId);
	}

	/**
	 * Получение пользователя по ID
	 * @param userId ID пользователя
	 * @return {@link UserDto}
	 */
	@GetMapping("/{userId}")
	UserDto getUser(@PathVariable @Positive Long userId) {
		return userService.getUser(userId);
	}

	/**
	 * Получение краткого списка пользователей
	 *
	 * @param userIds id пользователей
	 * @return {@link UserShortDto}
	 */
	@GetMapping("/short")
	public List<UserShortDto> getShortUsersByIds(@RequestParam List<@Positive Long> userIds) {
		return userService.getShortUsersByIds(userIds);
	}

}

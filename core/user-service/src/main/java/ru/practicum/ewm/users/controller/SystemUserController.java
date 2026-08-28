package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

}

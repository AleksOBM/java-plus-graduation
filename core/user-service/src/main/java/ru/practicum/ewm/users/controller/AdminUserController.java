package ru.practicum.ewm.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.user.come.NewUserRequest;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.ewm.users.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest,
	                          @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Добавление нового пользователя
				{} {}""", request.getMethod(), request.getRequestURI());

		return userService.createUser(newUserRequest);
	}

	@GetMapping("/{userId}")
	public UserDto getUser(@PathVariable Long userId, @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Получение пользователя по ID
				{} {}""", request.getMethod(), request.getRequestURI());

		return userService.getUser(userId);
	}

	/**
	 * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
	 * либо о конкретных (учитываются указанные идентификаторы)
	 * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список
	 *
	 * @param ids  id пользователей
	 * @param from количество элементов, которые нужно пропустить для формирования текущего набора
	 *             Default value : 0
	 * @param size количество элементов в наборе
	 *             Default value : 10
	 */
	@GetMapping
	public List<UserDto> findUsers(
			@RequestParam(required = false) List<Long> ids,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size,
			@NonNull HttpServletRequest request
	) {
		log.info("""
				ENDPOINT
				Получение информации о пользователях
				{} {}""", request.getMethod(), request.getRequestURI());

		return userService.findUsers(ids, from, size);
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId, @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Удаление пользователя
				{} {}""", request.getMethod(), request.getRequestURI());

		userService.deleteUser(userId);
	}

}

package ru.practicum.aggregation.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.dto.user.output.UserShortDto;

import java.util.List;

@FeignClient(
		name = "user-service",
		fallbackFactory = UserFallbackFactory.class
)
public interface UserClient {

	@GetMapping("/admin/users/{userId}")
	UserDto getUser(@PathVariable Long userId);

	@GetMapping("/admin/users/{userId}/check")
	void checkUser(@PathVariable Long userId);

	@GetMapping("/admin/users/short")
	List<UserShortDto> getShortUsersByIds(@RequestParam List<Long> userIds);

}

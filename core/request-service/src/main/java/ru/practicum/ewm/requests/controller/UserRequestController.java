package ru.practicum.ewm.requests.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.ewm.requests.service.RequestService;
import ru.practicum.stats.client.dto.ActionType;
import ru.practicum.stats.client.dto.UserActionDto;
import ru.practicum.stats.client.grpc.CollectorClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRequestController {

	RequestService requestService;
	CollectorClient collectorClient;

	/**
	 * В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
	 */
	@GetMapping
	public List<ParticipationRequestDto> findByRequesterId(@PathVariable @Positive Long userId,
	                                                       @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Получение информации о заявках текущего пользователя на участие в чужих событиях
				{} {}""", request.getMethod(), request.getRequestURI());

		return requestService.findByRequesterId(userId);
	}

	/**
	 * Обратите внимание:
	 * <p>
	 * нельзя добавить повторный запрос (Ожидается код ошибки 409)
	 * <p>
	 * инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
	 * <p>
	 * нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
	 * <p>
	 * если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
	 * <p>
	 * если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в
	 * состояние подтвержденного
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ParticipationRequestDto addParticipationRequest(@PathVariable @Positive Long userId,
	                                                       @RequestParam @Positive Long eventId,
	                                                       @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Добавление запроса от текущего пользователя на участие в событии
				{} {}""", request.getMethod(), request.getRequestURI());

		var result = requestService.addParticipationRequest(userId, eventId);

		log.info("Регистрация на участие в событии {} пользователеля {}", eventId, userId);
		collectorClient.collectUserAction(
				UserActionDto.builder()
						.userId(userId)
						.eventId(eventId)
						.actionType(ActionType.REGISTER)
						.timestamp(LocalDateTime.now())
						.build());

		return result;
	}

	@PatchMapping("/{requestId}/cancel")
	public ParticipationRequestDto cancelParticipationRequest(@PathVariable @Positive Long userId,
	                                                          @PathVariable @Positive Long requestId,
	                                                          @NonNull HttpServletRequest request) {
		log.info("""
				ENDPOINT
				Отмена своего запроса на участие в событии
				{} {}""", request.getMethod(), request.getRequestURI());

		return requestService.cancelParticipationRequest(userId, requestId);
	}

}

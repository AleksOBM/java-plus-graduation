package ru.practicum.ewm.requests.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.event.output.EventFullDto;
import ru.practicum.aggregation.dto.participation.come.EventRequestStatusUpdateRequest;
import ru.practicum.aggregation.dto.participation.output.EventRequestStatusUpdateResult;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.aggregation.dto.user.output.UserDto;
import ru.practicum.aggregation.enums.EventState;
import ru.practicum.aggregation.enums.ParticipationStatus;
import ru.practicum.aggregation.enums.RequestUpdateStatus;
import ru.practicum.aggregation.error.exception.bussines.cause.ConflictException;
import ru.practicum.aggregation.error.exception.bussines.cause.NotFoundException;
import ru.practicum.aggregation.model.repository.EventRequestCount;
import ru.practicum.aggregation.repository.EventFeignRepository;
import ru.practicum.aggregation.repository.UserFeignRepository;
import ru.practicum.ewm.requests.entity.ParticipationRequest;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {

	RequestRepository requestRepository;

	UserFeignRepository userFeignRepository;
	EventFeignRepository eventFeignRepository;

	public List<ParticipationRequestDto> findByUserIdAndEventId(long userId, long eventId) {
		var event = getEventFullDtoById(eventId);
		if (!event.initiator().id().equals(userId)) {
			throw new NotFoundException("Событие не найдено");
		}

		var requests = requestRepository.findByEventId(eventId);

		return requests.stream()
				.map(RequestMapper::toParticipationRequestDto)
				.toList();
	}

	public EventRequestStatusUpdateResult updateStatusRequest(
			long eventId,
			int participantLimit,
			boolean requestModeration,
			@NonNull EventRequestStatusUpdateRequest request
	) {
		var confirmedRequests = new ArrayList<ParticipationRequestDto>();
		var rejectedRequests = new ArrayList<ParticipationRequestDto>();

		boolean isModerationOff = !requestModeration || participantLimit == 0;

		if (isModerationOff || request.requestIds().isEmpty()) {
			return EventRequestStatusUpdateResult.builder()
					.confirmedRequests(Collections.emptyList())
					.rejectedRequests(Collections.emptyList())
					.build();
		}

		var countConfirmed = requestRepository
				.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);

		var requests = requestRepository.findAllByIdIn(request.requestIds());

		boolean isConfirmed = request.status().equals(RequestUpdateStatus.CONFIRMED);
		if (isConfirmed && countConfirmed >= participantLimit) {
			throw new ConflictException("Достигнут лимит подтвержденных заявок");
		}

		for (var pr : requests) {
			if (!pr.getStatus().equals(ParticipationStatus.PENDING)) {
				throw new ConflictException("Статус можно изменить только у заявок в состоянии рассмотрения");
			}

			if (isConfirmed && countConfirmed < participantLimit) {
				pr.setStatus(ParticipationStatus.CONFIRMED);
				countConfirmed++;
				confirmedRequests.add(RequestMapper.toParticipationRequestDto(pr));
			} else {
				pr.setStatus(ParticipationStatus.REJECTED);
				rejectedRequests.add(RequestMapper.toParticipationRequestDto(pr));
			}
		}

		requestRepository.saveAll(requests);

		// если в процессе лимит превышен - отклоняем все оставшиеся заявки
		if (isConfirmed && countConfirmed >= participantLimit) {
			if (requestRepository.rejectPendingRequests(eventId, ParticipationStatus.PENDING) < 0) {
				throw new RuntimeException("Не удалось отклонить заявку");
			}
		}

		return EventRequestStatusUpdateResult.builder()
				.confirmedRequests(confirmedRequests)
				.rejectedRequests(rejectedRequests)
				.build();
	}

	public List<ParticipationRequestDto> findByRequesterId(long userId) {
		return requestRepository.findByRequesterId(userId)
				.stream()
				.map(RequestMapper::toParticipationRequestDto)
				.toList();
	}

	public ParticipationRequestDto addParticipationRequest(long userId, long eventId) {
		var requester = getUserDtoById(userId);
		var event = getEventFullDtoById(eventId);

		if (!EventState.PUBLISHED.equals(event.state())) {
			throw new ConflictException("Нельзя участвовать в неопубликованном событии");
		}

		if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
			throw new ConflictException("Запрос уже существует");
		}

		if (event.initiator().id().equals(userId)) {
			throw new ConflictException(
					"Инициатор события не может добавить запрос на участие в своём событии");
		}

		int limit = event.participantLimit();
		if (limit != 0) {
			var confirmedCount = requestRepository
					.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);

			if (event.requestModeration()) {
				var pendingCount = requestRepository
						.countByEventIdAndStatus(eventId, ParticipationStatus.PENDING);
				if (confirmedCount + pendingCount >= limit) {
					throw new ConflictException("Достигнут лимит запросов на участие");
				}
			} else {
				if (confirmedCount >= limit) {
					throw new ConflictException("Достигнут лимит запросов на участие");
				}
			}
		}

		ParticipationStatus status;
		if (!event.requestModeration() || limit == 0) {
			status = ParticipationStatus.CONFIRMED;
		} else {
			status = ParticipationStatus.PENDING;
		}

		ParticipationRequest request = ParticipationRequest.builder()
				.requesterId(requester.id())
				.eventId(event.id())
				.status(status)
				.created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
				.build();

		return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
	}

	public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {
		ParticipationRequest request = getRequestById(requestId);

		if (!request.getRequesterId().equals(userId)) {
			throw new ConflictException("Нельзя отменить чужую заявку");
		}
		request.setStatus(ParticipationStatus.CANCELED);
		return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
	}

	@Override
	public List<EventRequestCount> getRequestsCount(List<Long> eventIds) {
		return requestRepository.getCountByEventIdsAndStatus(eventIds, ParticipationStatus.CONFIRMED);
	}

	@NonNull
	private UserDto getUserDtoById(long userId) {
		return userFeignRepository.getUserDtoById(userId);
	}

	@NonNull
	private EventFullDto getEventFullDtoById(long eventId) {
		int confirmets = requestRepository.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);
		return eventFeignRepository.systemFindEventById(eventId, confirmets);
	}

	@NonNull
	private ParticipationRequest getRequestById(long requestId) {
		return requestRepository.findById(requestId).orElseThrow(
				() -> new NotFoundException("Заявка с id=" + requestId + " не найдена")
		);
	}

}

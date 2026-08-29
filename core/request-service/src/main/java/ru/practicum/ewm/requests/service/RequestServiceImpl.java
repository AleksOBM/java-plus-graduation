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

	public List<ParticipationRequestDto> findByEventId(Long userId, Long eventId) {
		var event = getEventById(eventId);
		if (!event.initiator().id().equals(userId)) {
			throw new NotFoundException("Событие не найдено");
		}

		return requestRepository.findByEventId(eventId)
				.stream()
				.map(RequestMapper::toParticipationRequestDto)
				.toList();
	}

	public EventRequestStatusUpdateResult updateStatusRequest(Long eventId,
	                                                          @NonNull
	                                                          EventRequestStatusUpdateRequest request) {
		var event = getEventById(eventId);

		int limit = event.participantLimit();
		List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
		List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

		boolean isModerationOff = !event.requestModeration() || limit == 0;
		boolean idsEmpty = request.requestIds() == null || request.requestIds().isEmpty();

		if (isModerationOff || idsEmpty) {
			return EventRequestStatusUpdateResult.builder()
					.confirmedRequests(Collections.emptyList())
					.rejectedRequests(Collections.emptyList())
					.build();
		}

		int countConfirmed = requestRepository
				.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);
		List<ParticipationRequest> requests = requestRepository.findAllByIdIn(request.requestIds());

		if (request.status().name().equals(ParticipationStatus.CONFIRMED.name()) &&
				countConfirmed >= limit) {
			throw new ConflictException("Достигнут лимит подтвержденных заявок");
		}

		boolean confirmed = request.status().name().equals("CONFIRMED");

		for (ParticipationRequest pr : requests) {
			if (!pr.getStatus().equals(ParticipationStatus.PENDING)) {
				throw new ConflictException("Статус можно изменить только у заявок в состоянии рассмотрения");
			}

			if (confirmed && countConfirmed < limit) {
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
		if (confirmed && countConfirmed >= limit) {
			if (requestRepository.rejectPendingRequests(eventId, ParticipationStatus.PENDING) < 0) {
				throw new RuntimeException("Не удалось отклонить заявку");
			}
		}

		return EventRequestStatusUpdateResult.builder()
				.confirmedRequests(confirmedRequests)
				.rejectedRequests(rejectedRequests)
				.build();
	}

	public List<ParticipationRequestDto> findByRequesterId(Long userId) {
		return requestRepository.findByRequesterId(userId)
				.stream()
				.map(RequestMapper::toParticipationRequestDto)
				.toList();
	}

	public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
		UserDto requester = getUserById(userId);
		var event = getEventById(eventId);

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
			long confirmedCount = requestRepository
					.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);

			if (event.requestModeration()) {
				long pendingCount = requestRepository
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

	public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
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
	private UserDto getUserById(long userId) {
		return userFeignRepository.getUserDtoById(userId);
	}

	@NonNull
	private EventFullDto getEventById(long eventId) {
		int confirmets = requestRepository.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);
		return eventFeignRepository.systemFindEventById(eventId, confirmets);
	}

	@NonNull
	private ParticipationRequest getRequestById(Long requestId) {
		return requestRepository.findById(requestId).orElseThrow(
				() -> new NotFoundException("Заявка с id=" + requestId + " не найдена")
		);
	}

}

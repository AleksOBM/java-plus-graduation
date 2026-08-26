package ru.practicum.ewm.requests.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.aggregation.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.requests.entity.ParticipationRequest;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class RequestMapper {

	public ParticipationRequestDto toParticipationRequestDto(
			ParticipationRequest participationRequest) {

		if (participationRequest == null) {
			return null;
		}

		var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

		var dateTime = participationRequest.getCreated() == null ?
				null : participationRequest.getCreated().format(formatter);

		return ParticipationRequestDto.builder()
				.event(participationRequest.getEventId())
				.requester(participationRequest.getRequesterId())
				.status(participationRequest.getStatus())
				.created(dateTime)
				.id(participationRequest.getId())
				.build();
	}

}

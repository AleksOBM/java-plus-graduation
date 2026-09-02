package ru.practicum.ewm.requests.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.aggregation.dto.participation.output.ParticipationRequestDto;
import ru.practicum.ewm.requests.entity.ParticipationRequest;

@UtilityClass
public class RequestMapper {

	public ParticipationRequestDto toParticipationRequestDto(
			@NonNull ParticipationRequest participationRequest) {

		var dateTime = participationRequest.getCreated() == null ?
				null : participationRequest.getCreated();

		return ParticipationRequestDto.builder()
				.event(participationRequest.getEventId())
				.requester(participationRequest.getRequesterId())
				.status(participationRequest.getStatus())
				.created(dateTime)
				.id(participationRequest.getId())
				.build();
	}

}

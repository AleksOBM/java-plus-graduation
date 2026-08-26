package ru.practicum.ewm.events.service.rating;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.aggregation.dto.rating.RatingUpdateRequest;
import ru.practicum.aggregation.error.exception.NotFoundException;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.repository.EventRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingServiceImpl implements RatingService {

	EventRepository eventRepository;

	@Override
	public void updateRating(@NonNull RatingUpdateRequest request) {
		Event event = eventRepository.findById(request.eventId()).orElseThrow(() ->
				new NotFoundException("Event with id " + request.eventId() + " not found"));
		event.setRate(request.rate());
		eventRepository.save(event);
	}
}

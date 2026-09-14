package ru.practicum.stats.client.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc.RecommendationsControllerBlockingStub;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserRecommendationsRequestProto;
import ru.practicum.stats.client.dto.ActionsWeightsSum;
import ru.practicum.stats.client.dto.PredictedScore;
import ru.practicum.stats.client.dto.SimilarityCoefficient;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
@Slf4j
@Component
public class AnalyzerClient {

	@SuppressWarnings("unused")
	@GrpcClient("analyzer")
	private RecommendationsControllerBlockingStub client;

	// коэффициент сходства
	public List<SimilarityCoefficient> getSimilarEvents(long eventId, long userId, int maxResults) {
		SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
				.setEventId(eventId)
				.setUserId(userId)
				.setMaxResults(maxResults)
				.build();

		Iterator<RecommendedEventProto> iterator = client.getSimilarEvents(request);

		return asStream(iterator).map(proto ->
						SimilarityCoefficient.builder()
								.eventId(proto.getEventId())
								.score(proto.getScore())
								.build()
				)
				.toList();
	}

	// предсказанная оценка
	public List<PredictedScore> getRecomendationsForUser(long userId, int maxResults) {
		var request = UserRecommendationsRequestProto.newBuilder()
				.setUserId(userId)
				.setMaxResults(maxResults)
				.build();
		Iterator<RecommendedEventProto> iterator = client.getRecomendationsForUser(request);
		return asStream(iterator).map(recommendedEventProto ->
						PredictedScore.builder()
								.eventId(recommendedEventProto.getEventId())
								.score(recommendedEventProto.getScore())
								.build()
				)
				.toList();
	}

	// сумма весов действий с указанным мероприятием
	public List<ActionsWeightsSum> getInteractionsCount(List<Long> eventIds) {
		InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
				.addAllEventId(eventIds)
				.build();

		Iterator<RecommendedEventProto> iterator = client.getInteractionsCount(request);

		return asStream(iterator).map(proto ->
						ActionsWeightsSum.builder()
								.eventId(proto.getEventId())
								.score(proto.getScore())
								.build()
				)
				.toList();
	}

	@NonNull
	private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
				false
		);
	}

}
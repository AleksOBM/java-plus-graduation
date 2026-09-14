package ru.practicum.ewm.stats.analyzer.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.analyzer.service.InteractionsService;
import ru.practicum.ewm.stats.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.analyzer.service.SimilarityService;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc.RecommendationsControllerImplBase;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserRecommendationsRequestProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyzerController extends RecommendationsControllerImplBase {

	SimilarityService similarityService;
	InteractionsService interactionsService;
	RecommendationService recommendationService;

	/**
	 * возвращает поток рекомендованных мероприятий для указанного пользователя
	 */
	@Transactional(readOnly = true)
	@Override
	public void getRecomendationsForUser(@NonNull UserRecommendationsRequestProto request,
	                                     StreamObserver<RecommendedEventProto> responseObserver) {
		log.info("""
				Получен gRPC запрос: Получить рекомендации для пользователя
				{}""", request);

		try (var result = recommendationService.getRecomendationsForUser(
						request.getUserId(), request.getMaxResults())
		) {
			result.forEach(responseObserver::onNext);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error("Не удалось получить рекомендации для пользователя", e);
			responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
		}
	}

	/**
	 * возвращает поток мероприятий, с которыми не взаимодействовал этот пользователь,
	 * но которые максимально похожи на указанное мероприятие
	 */
	@Transactional(readOnly = true)
	@Override
	public void getSimilarEvents(@NonNull SimilarEventsRequestProto request,
	                             StreamObserver<RecommendedEventProto> responseObserver) {
		log.info("""
				Получен gRPC запрос: Получить похожие по мероприятиям
				{}""", request);

		try (var result = similarityService.getSimilarEvents(request)) {
			result.forEach(responseObserver::onNext);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error("Не удалось получить похожие по мероприятиям", e);
			responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
		}
	}

	/**
	 * получает идентификаторы мероприятий и возвращает их поток с суммой
	 * максимальных весов действий каждого пользователя с этими мероприятиями
	 */
	@Transactional(readOnly = true)
	@Override
	public void getInteractionsCount(@NonNull InteractionsCountRequestProto request,
	                                 StreamObserver<RecommendedEventProto> responseObserver) {
		log.info("""
				Получен gRPC запрос: Получить количество взаимодействий
				{}""", request.getEventIdList());

		try (var stream = interactionsService.getInteractionsByEventIds(request.getEventIdList())) {
			stream.forEach(responseObserver::onNext);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error("Не удалось получить количество взаимодействий", e);
			responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
		}
	}

}

package ru.practicum.ewm.stats.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.lang.NonNull;
import ru.practicum.ewm.stats.collector.service.UserActionService;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc.UserActionControllerImplBase;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorController extends UserActionControllerImplBase {

	private final UserActionService userActionService;

	@Override
	public void collectUserAction(@NonNull UserActionProto request,
	                              StreamObserver<Empty> responseObserver) {
		log.info("""
						Получен gRPC запрос:
						collectUserAction(UserActionProto request)
						{
							"user_id": "{}"
							"event_id": "{}",
							"action_type": "{}",
							"timestamp": "{}"
						}""",
				request.getUserId(),
				request.getEventId(),
				request.getActionType().name(),
				TimestampUtils.toLocalDateTime(request.getTimestamp())
		);

		try {
			userActionService.collectUserAction(request);
			responseObserver.onNext(Empty.getDefaultInstance());
			responseObserver.onCompleted();

		} catch (Exception e) {
			responseObserver.onError(
					new StatusRuntimeException(Status.fromThrowable(e))
			);
		}
	}

}

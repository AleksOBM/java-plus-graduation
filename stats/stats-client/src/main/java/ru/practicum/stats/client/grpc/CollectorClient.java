package ru.practicum.stats.client.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc.UserActionControllerFutureStub;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.client.dto.UserActionDto;
import ru.practicum.stats.client.mapper.ActionTypeMapper;
import ru.yandex.practicum.telemetry.utils.TimestampUtils;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Slf4j
@Component
public class CollectorClient {

	@GrpcClient("collector")
	private UserActionControllerFutureStub futureStub;

	public void collectUserAction(@NonNull UserActionDto dto) {
		var request = UserActionProto.newBuilder()
				.setUserId(dto.userId())
				.setEventId(dto.eventId())
				.setActionType(ActionTypeMapper.toProto(dto.actionType()))
				.setTimestamp(TimestampUtils.toTimestamp(dto.timestamp()))
				.build();

		ListenableFuture<Empty> listenableFuture = futureStub.collectUserAction(request);

		CompletableFuture<Empty> completableFuture = new CompletableFuture<>();

		listenableFuture.addListener(() -> {
			try {
				completableFuture.complete(listenableFuture.get());
			} catch (Exception e) {
				completableFuture.completeExceptionally(e);
			}
		}, MoreExecutors.directExecutor());
	}

}

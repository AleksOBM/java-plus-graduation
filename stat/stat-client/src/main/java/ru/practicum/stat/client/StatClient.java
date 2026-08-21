package ru.practicum.stat.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stat.client.exception.StatsServerUnavailable;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.StatsRequest;
import ru.practicum.stat.dto.ViewStatsDto;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class StatClient {

	private final RestTemplate rest;

	private final RetryTemplate retryTemplate;

	private final DiscoveryClient discoveryClient;

	public StatClient(@NonNull RestTemplateBuilder builder,
	                  @NonNull DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
		this.retryTemplate = createRetryTemplate();
		this.rest = builder.build();
	}

	public void hit(EndpointHitDto endpointHitDto) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);

			rest.exchange(
					makeUri("/hit"),
					HttpMethod.POST,
					requestEntity,
					Void.class
			);
		} catch (Exception e) {
			log.error("Ошибка записи: {}", endpointHitDto, e);
		}

	}

	public List<ViewStatsDto> getStat(StatsRequest statsRequest) {
		try {
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromUri(makeUri("/stats"))
					.queryParam("start", statsRequest.getStart())
					.queryParam("end", statsRequest.getEnd())
					.queryParam("unique", statsRequest.getUnique());

			List<String> uris = statsRequest.getUris();

			if (uris != null && !uris.isEmpty()) {
				builder.queryParam("uris", uris);
			}

			URI uri = builder.encode().build().toUri();

			return rest.exchange(
					uri,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<ViewStatsDto>>() {
					}
			).getBody();

		} catch (Exception e) {
			log.error("Ошибка записи: {}", statsRequest, e);
			return null;
		}
	}

	private ServiceInstance getInstance() {
		String statsServiceId = "stats-server";
		try {
			return discoveryClient
					.getInstances(statsServiceId)
					.getFirst();
		} catch (Exception exception) {
			throw new StatsServerUnavailable(
					"Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId,
					exception
			);
		}
	}

	@NonNull
	private URI makeUri(@NonNull String path) {
		ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
		return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
	}

	@NonNull
	private RetryTemplate createRetryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();

		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(3000L);
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

		MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
		retryPolicy.setMaxAttempts(3);
		retryTemplate.setRetryPolicy(retryPolicy);

		return retryTemplate;
	}
}

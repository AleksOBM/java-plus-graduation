package ru.practicum.ewm.requests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.aggregation.client.event.EventClient;
import ru.practicum.aggregation.client.request.RequestClient;
import ru.practicum.aggregation.client.user.UserClient;

@SpringBootApplication
@EnableFeignClients(clients = {
		UserClient.class,
		EventClient.class,
		RequestClient.class
})
@ComponentScan(value = {
		"ru.practicum.ewm.requests",
		"ru.practicum.aggregation.client",
		"ru.practicum.aggregation.error",
		"ru.practicum.aggregation.repository"
})
public class RequestServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(RequestServiceApp.class, args);
	}
}

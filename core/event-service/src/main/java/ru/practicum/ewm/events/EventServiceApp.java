package ru.practicum.ewm.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.aggregation.client.event.EventClient;
import ru.practicum.aggregation.client.request.RequestClient;
import ru.practicum.aggregation.client.user.UserClient;
import ru.practicum.aggregation.config.JacksonConfiguration;

@SpringBootApplication
@EnableFeignClients(clients = {
		UserClient.class,
		EventClient.class,
		RequestClient.class
})
@ComponentScan(value = {
		"ru.practicum.ewm.events",
		"ru.practicum.aggregation.config",
		"ru.practicum.aggregation.client",
		"ru.practicum.aggregation.error",
		"ru.practicum.aggregation.repository",
		"ru.practicum.stats.client.grpc"
})
@ConfigurationPropertiesScan(basePackageClasses = JacksonConfiguration.class)
public class EventServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(EventServiceApp.class, args);
	}
}

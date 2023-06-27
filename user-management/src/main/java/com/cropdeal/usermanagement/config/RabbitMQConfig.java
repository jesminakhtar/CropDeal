package com.cropdeal.usermanagement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue loginEventQueue() {
        return new Queue("login_event_queue");
    }

    @Bean
    public Queue registrationEventQueue() {
        return new Queue("registration_event_queue");
    }

    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange("user_events_exchange");
    }

    @Bean
    public Binding loginEventBinding(Queue loginEventQueue, DirectExchange userEventsExchange) {
        return BindingBuilder.bind(loginEventQueue).to(userEventsExchange).with("login_event_routing_key");
    }

    @Bean
    public Binding registrationEventBinding(Queue registrationEventQueue, DirectExchange userEventsExchange) {
        return BindingBuilder.bind(registrationEventQueue).to(userEventsExchange).with("registration_event_routing_key");
    }
}

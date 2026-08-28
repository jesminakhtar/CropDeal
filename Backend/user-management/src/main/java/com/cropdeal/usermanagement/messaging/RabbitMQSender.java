//package com.cropdeal.usermanagement.messaging;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RabbitMQSender {
//    private final RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    public void sendLoginMessage(String message) {
//        rabbitTemplate.convertAndSend("user_events_exchange", "login_event_routing_key", message);
//    }
//    
//    public void sendRegistrationMessage(String message) {
//        rabbitTemplate.convertAndSend("user_events_exchange", "registration_event_routing_key", message);
//    }
//}
//

//package com.cropdeal.usermanagement.messaging;
//
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RabbitMQListener {
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public RabbitMQListener(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @RabbitListener(queues = "login_event_queue")
//    public void processMessage(String message) {
//        messagingTemplate.convertAndSend("/topic/messages", message);
//    }
//}

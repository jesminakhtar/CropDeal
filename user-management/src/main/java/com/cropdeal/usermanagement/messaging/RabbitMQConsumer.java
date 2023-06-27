package com.cropdeal.usermanagement.messaging;
//package com.cropdeal.usermanagement.rabbitmq;
//
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.DeliverCallback;
//
//public class RabbitMQConsumer {
//    private final static String QUEUE_NAME = "TestQueue"; // Replace with the name of your queue
//    
//    public static void main(String[] args) throws Exception {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost"); // Replace with the hostname of your RabbitMQ server
//        
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//            
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//            System.out.println("Waiting for messages. To exit, press CTRL+C");
//            
//            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                String message = new String(delivery.getBody(), "UTF-8");
//                System.out.println("Received message: " + message);
//            };
//            
//            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
//            
//            // Keep the application running to continue consuming messages
//            Thread.sleep(1000);
//        }
//    }
//}

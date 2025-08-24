package ru.yofujitsu.payment_processing.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${payment-authorization-response.queue.name}")
    private String bankPaymentAuthorizationResponseQueue;

    @Value("${final-payment-status.queue.name}")
    private String finalPaymentStatusResponseQueue;

    @Value("${logging.queue.name}")
    private String loggingQueue;

    @Value("${notification.queue.name}")
    private String notificationQueue;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public CachingConnectionFactory getConnectionFactory() {
        var connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        var rabbitTemplate = new RabbitTemplate(getConnectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue paymentAuthResponseQueue() {
        var queue = new Queue(bankPaymentAuthorizationResponseQueue);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Queue finaPaymentStatusQueue() {
        var queue = new Queue(finalPaymentStatusResponseQueue);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Queue loggingQueue() {
        var queue = new Queue(loggingQueue);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Queue notificationQueue() {
        var queue = new Queue(notificationQueue);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        var converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(false);
        return converter;
    }
}
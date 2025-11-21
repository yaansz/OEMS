package dev.yaansz.MessageSender.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Consumer queue - receives messages from MessageRegister
    public static final String MESSAGE_EXCHANGE = "messages.exchange";
    public static final String MESSAGE_QUEUE = "messages.queue";
    public static final String MESSAGE_ROUTING_KEY = "message.email";

    // Producer queue - sends success responses back to MessageRegister
    public static final String SUCCESS_EXCHANGE = "messages.success.exchange";
    public static final String SUCCESS_QUEUE = "messages.success.queue";
    public static final String SUCCESS_ROUTING_KEY = "message.success";

    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(MESSAGE_EXCHANGE, true, false);
    }

    @Bean
    public Queue messageQueue() {
        return QueueBuilder.durable(MESSAGE_QUEUE)
                .withArgument("x-dead-letter-exchange", MESSAGE_EXCHANGE + ".dlx")
                .build();
    }

    @Bean
    public Binding messageBinding(Queue messageQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageQueue)
                .to(messageExchange)
                .with(MESSAGE_ROUTING_KEY);
    }

    @Bean
    public TopicExchange successExchange() {
        return new TopicExchange(SUCCESS_EXCHANGE, true, false);
    }

    @Bean
    public Queue successQueue() {
        return QueueBuilder.durable(SUCCESS_QUEUE).build();
    }

    @Bean
    public Binding successBinding(Queue successQueue, TopicExchange successExchange) {
        return BindingBuilder.bind(successQueue)
                .to(successExchange)
                .with(SUCCESS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}

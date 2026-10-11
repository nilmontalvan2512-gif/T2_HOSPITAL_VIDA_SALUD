package pe.edu.hospitalvidasalud.notifications;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {
    @Bean
    TopicExchange hospitalEventsExchange() {
        return new TopicExchange("hospital.events", true, false);
    }

    @Bean
    Queue appointmentNotificationQueue() {
        return new Queue("notifications.appointment-confirmed", true);
    }

    @Bean
    Binding appointmentNotificationBinding(Queue appointmentNotificationQueue,
                                           TopicExchange hospitalEventsExchange) {
        return BindingBuilder.bind(appointmentNotificationQueue)
                .to(hospitalEventsExchange)
                .with("appointment.confirmed");
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

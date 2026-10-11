package pe.edu.hospitalvidasalud.appointments;

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
    public static final String EXCHANGE = "hospital.events";
    public static final String APPOINTMENT_CONFIRMED_KEY = "appointment.confirmed";
    public static final String NOTIFICATION_QUEUE = "notifications.appointment-confirmed";

    @Bean
    TopicExchange hospitalEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue appointmentNotificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    Binding appointmentNotificationBinding(Queue appointmentNotificationQueue,
                                           TopicExchange hospitalEventsExchange) {
        return BindingBuilder.bind(appointmentNotificationQueue)
                .to(hospitalEventsExchange)
                .with(APPOINTMENT_CONFIRMED_KEY);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

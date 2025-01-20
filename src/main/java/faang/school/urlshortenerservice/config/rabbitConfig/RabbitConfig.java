package faang.school.urlshortenerservice.config.rabbitConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue-name}")
    private String validationQueueName;

    @Value("${rabbitmq.exchange-name}")
    private String validationExchangeName;

    @Value("${rabbitmq.routing-key}")
    private String validationRoutingKey;

    @Bean
    public Queue validationQueue() {
        return new Queue(validationQueueName, true);
    }

    @Bean
    public DirectExchange validationExchange() {
        return new DirectExchange(validationExchangeName);
    }

    @Bean
    public Binding validationBinding(Queue validationQueue, DirectExchange validationExchange) {
        return BindingBuilder
                .bind(validationQueue)
                .to(validationExchange)
                .with(validationRoutingKey);
    }
}

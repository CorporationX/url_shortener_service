package faang.school.urlshortenerservice.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitQueueProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange-name}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void sendUrlIdForValidation(String urlId) {
        log.info("Sending URL with ID {} for validation", urlId);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, urlId);
    }
}

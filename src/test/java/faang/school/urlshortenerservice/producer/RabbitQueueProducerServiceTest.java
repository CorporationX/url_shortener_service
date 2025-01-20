package faang.school.urlshortenerservice.producer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitQueueProducerServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitQueueProducerService rabbitQueueProducerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rabbitQueueProducerService, "exchangeName", "test-exchange");
        ReflectionTestUtils.setField(rabbitQueueProducerService, "routingKey", "test-routing-key");
    }

    @Test
    void testSendUrlIdForValidation() {
        String urlId = "123abc";
        rabbitQueueProducerService.sendUrlIdForValidation(urlId);
        verify(rabbitTemplate, times(1)).convertAndSend("test-exchange", "test-routing-key", urlId);
    }
}

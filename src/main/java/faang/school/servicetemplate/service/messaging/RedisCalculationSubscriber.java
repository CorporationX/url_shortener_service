package faang.school.servicetemplate.service.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.servicetemplate.model.CalculationRequest;
import faang.school.servicetemplate.service.worker.CalculationWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisCalculationSubscriber implements MessageListener {

    private final CalculationWorker calculationWorker;

    private final boolean processingEnabled;

    @Autowired
    public RedisCalculationSubscriber(
            CalculationWorker calculationWorker,
            @Value("${calculations_messages_processing.enabled}") boolean processingEnabled
    ) {
        this.calculationWorker = calculationWorker;
        this.processingEnabled = processingEnabled;
    }

    public List<CalculationRequest> receivedRequests = new ArrayList<>();

    public void onMessage(final Message message, final byte[] pattern) {
        log.info("Message received: " + new String(message.getBody()));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var calculationRequest = objectMapper.readValue(message.getBody(), CalculationRequest.class);
            receivedRequests.add(calculationRequest);
            if (processingEnabled) {
                calculationWorker.processCalculationAsync(calculationRequest);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

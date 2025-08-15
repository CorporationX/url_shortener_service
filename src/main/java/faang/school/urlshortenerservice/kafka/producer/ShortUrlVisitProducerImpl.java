package faang.school.urlshortenerservice.kafka.producer;

import faang.school.urlshortenerservice.kafka.dto.ShortUrlVisitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ShortUrlVisitProducerImpl implements ShortUrlVisitProducer {
    @Value("${spring.kafka.topics.short-url-visit.name}")
    private String topic;

    private final KafkaTemplate<String, Object> template;

    public void onVisit(ShortUrlVisitDto dto) {
        template.send(topic, dto);
    }
}

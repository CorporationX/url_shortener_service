package faang.school.urlshortenerservice.publisher;

import faang.school.urlshortenerservice.event.LowChacheEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowCachePublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent() {
        eventPublisher.publishEvent(new LowChacheEvent(this));
    }
}

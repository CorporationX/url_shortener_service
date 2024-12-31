package faang.school.urlshortenerservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LowCacheEvent extends ApplicationEvent {
    public LowCacheEvent(Object source) {
        super(source);
    }
}
package faang.school.urlshortenerservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LowChacheEvent extends ApplicationEvent {

    public LowChacheEvent(Object source) {
        super(source);
    }
}

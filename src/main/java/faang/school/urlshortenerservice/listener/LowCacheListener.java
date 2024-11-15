package faang.school.urlshortenerservice.listener;

import faang.school.urlshortenerservice.event.LowCacheEvent;
import faang.school.urlshortenerservice.hash.HashCacheFiller;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowCacheListener {
    private final HashCacheFiller hashCacheFiller;

    @EventListener
    public void handleLowCacheEvent(LowCacheEvent event) {
        hashCacheFiller.fillCache();
    }
}

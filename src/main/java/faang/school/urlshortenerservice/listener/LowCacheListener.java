package faang.school.urlshortenerservice.listener;

import faang.school.urlshortenerservice.event.LowCacheEvent;
import faang.school.urlshortenerservice.hash.HashCacheFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowCacheListener {

    private final HashCacheFilter hashCacheFilter;

    @EventListener
    public void handleLowCacheEvent(LowCacheEvent event) {
        hashCacheFilter.fillCache();
    }
}

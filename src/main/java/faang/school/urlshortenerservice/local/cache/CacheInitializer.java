package faang.school.urlshortenerservice.local.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInitializer {

    private final LocalCache localCache;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        localCache.init();
    }
}

package faang.school.urlshortenerservice.listener;

import faang.school.urlshortenerservice.model.UrlCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisKeyExpiredEventListener {

    private final TtlExpiredHashStorage ttlExpiredHashStorage;

    @EventListener(RedisKeyExpiredEvent.class)
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<UrlCache> event) {
        UrlCache urlCache = (UrlCache) event.getValue();
        if (urlCache != null) {
            ttlExpiredHashStorage.addHash(urlCache.getHash());
            log.debug("Expired hash: {}", urlCache.getHash());
        }
    }
}
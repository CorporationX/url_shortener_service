package faang.school.urlshortenerservice.publisher;


import com.google.gson.Gson;
import faang.school.urlshortenerservice.event.ShortUrlCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlEventPublisher {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.channels.url-short.name}")
    private String channel;

    public void publishShortUrlCreated(String hash, String originalUrl, String userId) {
        ShortUrlCreatedEvent event = ShortUrlCreatedEvent.builder()
                .hash(hash)
                .originalUrl(originalUrl)
                .userId(userId)
                .build();
        String jsonEvent = new Gson().toJson(event);

        redisTemplate.convertAndSend(channel, jsonEvent);
    }
}

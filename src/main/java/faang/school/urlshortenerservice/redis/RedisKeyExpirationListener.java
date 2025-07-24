package faang.school.urlshortenerservice.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "url_mapping:";
    private static final String URL_MAPPINGS_COUNT_KEY = "url_mappings_count";

    public RedisKeyExpirationListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.debug("Received expired key event for: {}", expiredKey);

        if (expiredKey.startsWith(KEY_PREFIX)) {
            Long newCount = redisTemplate.opsForValue().decrement(URL_MAPPINGS_COUNT_KEY);
            log.info("Key {} expired. Decremented URL mappings count to: {}", expiredKey, newCount);
        }
    }
}
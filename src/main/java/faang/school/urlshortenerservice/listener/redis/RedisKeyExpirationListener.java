package faang.school.urlshortenerservice.listener.redis;

import faang.school.urlshortenerservice.repository.cassandra.UrlHashRepository;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private static final String URL_MAPPINGS_COUNT_KEY = "url_mappings_count";
    private final RedisTemplate<String, String> redisTemplate;
    private final UrlHashRepository urlHashRepository;
    private final PreparedUrlHashRepository preparedUrlHashRepository;

    public RedisKeyExpirationListener(RedisTemplate<String, String> redisTemplate,
                                      UrlHashRepository urlHashRepository,
                                      PreparedUrlHashRepository preparedUrlHashRepository) {
        this.redisTemplate = redisTemplate;
        this.urlHashRepository = urlHashRepository;
        this.preparedUrlHashRepository = preparedUrlHashRepository;
    }

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        Long newCount = redisTemplate.opsForValue().decrement(URL_MAPPINGS_COUNT_KEY);
        log.info("Key {} expired. Decremented URL mappings count to: {}", expiredKey, newCount);

        urlHashRepository.freeReusedEntityByHash(expiredKey);
        preparedUrlHashRepository.markHashAsReusable(expiredKey);
    }
}
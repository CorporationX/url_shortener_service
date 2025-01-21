package faang.school.urlshortenerservice.consumer;

import faang.school.urlshortenerservice.model.enums.UrlStatus;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.MaliciousHostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitQueueConsumerService {

    private final MaliciousHostValidator validator;
    private final RedisTemplate<String, String> blackListRedisTemplate;
    private final UrlRepository urlRepository;

    @RabbitListener(queues = "${rabbitmq.queue-name}")
    public void handleMessage(String urlId) {
        log.info("Received URL with ID {} for validation", urlId);

        urlRepository.findById(urlId).ifPresentOrElse(urlEntity -> {
                    String longUrl = urlEntity.getUrl();
                    if (!validator.isHostSafe(longUrl)) {
                        try {
                            String host = new URL(longUrl).getHost().toLowerCase();
                            blackListRedisTemplate.opsForValue().set(host, "blacklisted");
                            log.warn("Host {} is unsafe and added to blacklist cache", host);
                            urlEntity.setStatus(UrlStatus.BLOCKED);
                            urlRepository.save(urlEntity);
                        } catch (Exception e) {
                            log.error("Error processing URL {} for blacklist", longUrl, e);
                        }
                    } else {
                        urlEntity.setStatus(UrlStatus.OK);
                        log.info("URL {} is safe", longUrl);
                    }
                },
                () -> log.warn("URL with ID {} not found", urlId));
    }
}

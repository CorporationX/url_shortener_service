package faang.school.urlshortenerservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ScheduledTopHundredCache {
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "${spring.data.redis.top-hundred-refresh}")
    public void cacheTopHundred() {
        Set<Object> raw = redisTemplate.opsForZSet().reverseRange("popular:urls", 0, 99);
        List<String> top100 = raw == null ? List.of() : raw.stream().map(Object::toString).toList();
        redisTemplate.opsForValue()
                .set("popular:top100", top100);
    }
}

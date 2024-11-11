package faang.school.urlshortenerservice.service.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheService<T> {

    void put(String key, T value);

    void put(String key, T value, Duration time);

    void expire(String key, Duration duration);

    Optional<T> getValue(String key, Class<T> clazz);

    long getExpire(String key, TimeUnit timeUnit);

    boolean delete(String key);

    long incrementAndGet(String key);

    long addExpire(String key, Duration duration);
}

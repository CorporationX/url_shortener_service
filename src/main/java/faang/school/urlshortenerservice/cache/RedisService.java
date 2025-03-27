package faang.school.urlshortenerservice.cache;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Optional;

public interface RedisService {
    <T> void save(String key, T value);

    <T> Optional<T> get(String key, Class<T> clazz);

    <T> Optional<T> get(String key, TypeReference<T> typeReference);

    void delete(String key);
}

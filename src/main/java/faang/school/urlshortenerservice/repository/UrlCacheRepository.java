package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.RedisUrl;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link RedisUrl} в кэше (Redis).
 * Предоставляет методы для поиска и управления данными в Redis.
 */
public interface UrlCacheRepository extends KeyValueRepository<RedisUrl, Long> {

    /**
     * Находит запись в Redis по хэшу.
     *
     * @param hash Хэш короткого URL.
     * @return {@link Optional}, содержащий {@link RedisUrl}, если запись найдена, иначе пустой {@link Optional}.
     */
    Optional<RedisUrl> findByHash(String hash);
}

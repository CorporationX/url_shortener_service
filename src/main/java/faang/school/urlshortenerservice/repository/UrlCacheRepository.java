package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.RedisUrl;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface UrlCacheRepository extends KeyValueRepository<RedisUrl, Long> {
    Optional<RedisUrl> findByHash(String hash);
}
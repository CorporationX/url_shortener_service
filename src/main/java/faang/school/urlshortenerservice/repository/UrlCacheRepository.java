package faang.school.urlshortenerservice.repository;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository extends Red{
}

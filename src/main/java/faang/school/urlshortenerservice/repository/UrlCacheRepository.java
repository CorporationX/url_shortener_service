package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlCache;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@RedisHash("UrlCache")
public interface UrlCacheRepository extends CrudRepository<UrlCache, String> {
}

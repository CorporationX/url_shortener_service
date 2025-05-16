package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.RedisUrl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface UrlCacheRepository extends CrudRepository<RedisUrl, String> {
    RedisUrl findByHash(String hash);
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlCache;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableRedisRepositories
public interface UrlCacheRepository extends CrudRepository<UrlCache, String> {

    UrlCache findByUrl(String url);
}
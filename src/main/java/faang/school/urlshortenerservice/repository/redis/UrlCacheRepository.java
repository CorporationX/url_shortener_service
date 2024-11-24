package faang.school.urlshortenerservice.repository.redis;

import faang.school.urlshortenerservice.model.UrlCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository extends CrudRepository<UrlCache, String> {
}

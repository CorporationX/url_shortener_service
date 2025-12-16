package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.CachedUrl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository extends CrudRepository<CachedUrl, String> {
}

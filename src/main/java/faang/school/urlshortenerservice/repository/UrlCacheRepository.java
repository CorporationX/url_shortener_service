package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlHashRedis;
import org.springframework.data.repository.CrudRepository;

public interface UrlCacheRepository extends CrudRepository<UrlHashRedis, String> {
}

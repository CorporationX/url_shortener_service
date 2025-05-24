package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRedisRepository extends CrudRepository<UrlRedis, String> {
}

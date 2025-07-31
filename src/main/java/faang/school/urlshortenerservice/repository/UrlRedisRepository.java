package faang.school.urlshortenerservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import faang.school.urlshortenerservice.model.UrlRedis;

@Repository
public interface UrlRedisRepository extends CrudRepository<UrlRedis, String> {

}

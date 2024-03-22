package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlCash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableRedisRepositories
public interface UrlCashRepository extends CrudRepository<UrlCash, String> {

    UrlCash findByUrl(String url);
}
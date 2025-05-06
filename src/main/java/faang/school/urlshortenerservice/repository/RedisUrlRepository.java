package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface RedisUrlRepository extends CrudRepository<RedisCashUrl,String> {
    RedisCashUrl findByHash(String hash);
}

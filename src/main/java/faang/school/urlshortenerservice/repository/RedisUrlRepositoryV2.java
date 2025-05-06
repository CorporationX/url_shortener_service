package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.RedisUrl;
import org.springframework.data.repository.CrudRepository;

public interface RedisUrlRepositoryV2 extends CrudRepository<RedisUrl, String> {
    RedisUrl findByHash(String hash);
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlInRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository extends CrudRepository<UrlInRedis, String> {

}

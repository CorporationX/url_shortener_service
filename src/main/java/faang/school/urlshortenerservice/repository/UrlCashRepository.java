package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCashRepository extends CrudRepository<UrlCache, String> {
}

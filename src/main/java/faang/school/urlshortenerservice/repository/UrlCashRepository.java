package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlCache;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCashRepository extends CrudRepository<UrlCache, String> {
    UrlCache findByUrl(@NotNull String url);
}

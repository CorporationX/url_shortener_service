package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UrlCacheRepository extends CrudRepository<Url, String> {
    Optional<Url> findByUrl(String url);
}


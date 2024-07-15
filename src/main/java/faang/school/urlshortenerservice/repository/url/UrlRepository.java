package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.url.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {
    Optional<Url> findByHash(String hash);
}

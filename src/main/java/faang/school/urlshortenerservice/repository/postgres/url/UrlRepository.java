package faang.school.urlshortenerservice.repository.postgres.url;

import faang.school.urlshortenerservice.model.url.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);
}

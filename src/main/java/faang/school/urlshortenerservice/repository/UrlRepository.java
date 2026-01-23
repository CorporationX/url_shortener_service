package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    default Url findByHashOrThrow(String hash) {
        return findByHash(hash)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));
    }
    Optional<Url> findByHash(String hash);
    Optional<Url> findByUrl(String url);
    boolean existsByHash(String hash);
    boolean existsByUrl(String url);

}

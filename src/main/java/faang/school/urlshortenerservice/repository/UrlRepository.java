package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlHash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlHash, String> {

    Optional<UrlHash> findByUrl(String url);
}

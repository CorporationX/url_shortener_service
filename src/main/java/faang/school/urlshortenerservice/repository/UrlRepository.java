package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlHash, String> {

    Optional<UrlHash> findByUrl(String url);

    @Query(value = "DELETE FROM url WHERE created_at < now() - interval '1 year' RETURNING hash", nativeQuery = true)
    List<String> findAndDeleteOldUrl();
}

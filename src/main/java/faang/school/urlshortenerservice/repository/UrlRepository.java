package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.dto.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash", nativeQuery = true)
    List<String> removeOldUrls();

    Optional<Url> findUrlByHash(String hash);
}

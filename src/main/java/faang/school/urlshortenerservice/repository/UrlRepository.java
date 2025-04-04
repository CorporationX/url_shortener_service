package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    @Query(value = "DELETE FROM url u WHERE u.created_at < :clearOldUrlsTime RETURNING u.hash", nativeQuery = true)
    List<String> deleteOldUrlsAndGetReleasedHashes(@Param("clearOldUrlsTime") LocalDateTime clearOldUrlsTime);
}
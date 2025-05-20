package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    boolean existsByOriginalUrl(String originalUrl);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM url WHERE created_at < :date RETURNING hash", nativeQuery = true)
    List<String> deleteOldHashes(@Param("date") LocalDateTime date);
}


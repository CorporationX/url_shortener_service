package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Transactional

    @Query(value = "DELETE FROM url WHERE created_at < :timeLimit RETURNING hash", nativeQuery = true)
    List<Hash> deleteExpiredUrlsAndReturnHashes(@Param("timeLimit") LocalDateTime timeLimit);

    boolean existsByUrl(String url);

    @Query(value = "SELECT url FROM url WHERE hash = :hash", nativeQuery = true)
    Optional<String> findByHash(String hash);
}

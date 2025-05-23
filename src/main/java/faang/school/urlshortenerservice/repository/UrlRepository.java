package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM url_mapping WHERE created_at < NOW() - INTERVAL '1 YEAR' RETURNING hash_value",
            nativeQuery = true)
    List<String> deleteUrlsOlderThanOneYearAndGetHashes();

    @Query("SELECT u.originalUrl FROM UrlMapping u WHERE u.hashValue = :hash")
    Optional<String> findOriginalUrlByHash(@Param("hash") String hash);
}
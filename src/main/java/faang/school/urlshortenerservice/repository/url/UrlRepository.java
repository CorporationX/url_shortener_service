package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.model.url.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM url WHERE created_at < :timeLimit RETURNING hash", nativeQuery = true)
    List<String> deleteExpiredUrlsAndReturnHashes(@Param("timeLimit") LocalDateTime timeLimit);

    @Transactional(readOnly = true)
    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    Optional<String> findUrlByHash(@Param("hash") String hash);

    Optional<Url> findByUrl(String url);

    @Query("SELECT h.hash FROM Url h WHERE h.hash = :url")
    String findHashByUrl(@Param("url") String url);
}
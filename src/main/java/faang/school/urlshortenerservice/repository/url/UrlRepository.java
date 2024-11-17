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
    @Query(value = "DELETE FROM url WHERE created_at < :oneYearAgo RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes(@Param("oneYearAgo") LocalDateTime oneYearAgo);

    @Transactional(readOnly = true)
    Optional<String> findUrlByHash(String hash);
}
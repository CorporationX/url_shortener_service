package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query("SELECT h.url FROM Hash h WHERE h.hash = :hash")
    Url getUrlByHash(@Param("hash") String hash);

    @Query(value = "DELETE FROM url WHERE created_at < NOW() - INTERVAL :interval RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes(@Param("interval") String interval);
}

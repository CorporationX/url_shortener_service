package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(value = """
            DELETE FROM url
            WHERE created_at < (CURRENT_TIMESTAMP - CAST(:retentionPeriod AS INTERVAL))
            RETURNING hash
            """, nativeQuery = true)
    List<String> deleteOldLinks(@Param("retentionPeriod") String retentionPeriod);

    @Query(value = """
            SELECT u.url FROM Url u
            WHERE u.hash = :hash
            """)
    String getUrlByHash(@Param("hash") String hash);

    @Modifying
    @Query(value = """
            INSERT INTO url (hash, url, created_at)
            VALUES (:hash, :url, CURRENT_TIMESTAMP)
            """, nativeQuery = true)
    void saveUrlWithNewHash(@Param("hash") String hash, @Param("url") String url);

}


package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM url
            WHERE created_at < (CURRENT_TIMESTAMP - INTERVAL '1 year')
            RETURNING hash
            """, nativeQuery = true)
    List<String> deleteOldLinks();

    @Query(value = """
            SELECT u.url FROM Url u
            WHERE u.hash = :hash
            """)
    String getUrlByHash(@Param("hash") String hash);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO url (hash, url, created_at)
            VALUES (:hash, :url, CURRENT_TIMESTAMP)
            """, nativeQuery = true)
    void saveUrlWithNewHash(@Param("hash") String hash, @Param("url") String url);
}


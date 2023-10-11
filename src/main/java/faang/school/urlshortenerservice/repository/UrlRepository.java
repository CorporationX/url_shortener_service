package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL '1 year'
            RETURNING hash_value
            """)
    List<String> deleteExpiredHashes();

    @Query("SELECT u.url FROM Url u WHERE u.hash = :url")
    String findUrlByHash(@Param("hash") String hash);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO url (hash, url) VALUES (:hash, :url)", nativeQuery = true)
    void save(@Param("hash") String hash, @Param("url") String url);
}

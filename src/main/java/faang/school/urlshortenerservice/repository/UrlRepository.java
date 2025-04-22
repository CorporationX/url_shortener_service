package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(value = """
           SELECT * FROM url WHERE created_at < ? 
            """, nativeQuery = true)
    List<Url> findAllExpiredUrls(LocalDateTime threshold);

    @Query(nativeQuery = true, value = """
             SELECT url FROM url WHERE hash = ?
            """)
    String getUrl(String hash);

    @Query(nativeQuery = true, value = """
            SELECT hash FROM url WHERE url = ?
            """)
    String getHash(String url);
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT hash FROM short_url
            WHERE expiration_time < NOW()
            FOR UPDATE SKIP LOCKED
            LIMIT :limit""")
    List<String> findExpiredUrlsHashes(@Param("limit") int limit);

    @Query(nativeQuery = true, value = """
    SELECT COUNT(*) FROM url u
    WHERE  u.expiration_time < NOW()
            """)
    Integer countOfOldUrl();
    String findByUrl(String url);
}

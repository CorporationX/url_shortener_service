package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE url.hash IN(
                SELECT url.hash FROM url WHERE url.created_at < now() - INTERVAL '1 year' FOR UPDATE
            ) RETURNING hash
            """)
    List<String> cleanExpiredUrls();
}

package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.url.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true,
            value = """
                    DELETE FROM url u
                     WHERE u.created_at < current_timestamp - INTERVAL '1 year'
                    RETURNING u.*
                    """)
    List<Url> findAndReturnExpiredUrls();

    Optional<Url> findByUrlIgnoreCase(String url);

    Optional<Url> findByHashIgnoreCase(String hash);
}

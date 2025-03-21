package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            DELETE FROM url u
            WHERE e.created_at <= :date
            RETURNING u.hash
            """)
    List<String> deleteOutdatedUrls(LocalDateTime date);

    Optional<Url> findByHash(String hash);
}

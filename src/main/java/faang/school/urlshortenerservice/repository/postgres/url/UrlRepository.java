package faang.school.urlshortenerservice.repository.postgres.url;

import faang.school.urlshortenerservice.model.url.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url 
            WHERE hash IN (SELECT hash FROM url WHERE created_at < :date FOR UPDATE) 
            RETURNING hash
            """)
    List<String> getOldUrlsAndDelete(@Param("date") LocalDateTime date);
}

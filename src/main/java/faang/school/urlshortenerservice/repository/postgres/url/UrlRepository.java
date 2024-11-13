package faang.school.urlshortenerservice.repository.postgres.url;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at < :date RETURNING hash
            """)
    List<String> deleteAndReturnOldUrls(@Param("date") LocalDateTime date);

    Optional<Url> findByHash(String hash);
}

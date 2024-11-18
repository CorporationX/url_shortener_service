package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByLongUrl(String longUrl);

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < :expirationDate
        RETURNING hash
        """)
    List<String> deleteAllByCreatedAtBefore(LocalDateTime expirationDate);
}

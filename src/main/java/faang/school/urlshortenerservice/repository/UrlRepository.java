package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<String> getByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE * FROM url
            WHERE created_at < :targetDate
            RETURNING *
            """)
    List<Url> getAndDeleteAllByCreatedAtBefore(LocalDateTime targetDate);
}

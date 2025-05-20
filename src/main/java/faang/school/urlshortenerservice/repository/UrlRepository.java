package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < :createdAt
        RETURNING hash
    """)
    List<String> deleteByCreatedAtBefore(LocalDateTime createdAt);
}

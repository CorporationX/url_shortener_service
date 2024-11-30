package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at <= current_date - INTERVAL '1 year'
            RETURNING hash
            """)
    List<String> getHashAndDeleteURL();

    Optional<Url> findUrlByHash(String hash);
}

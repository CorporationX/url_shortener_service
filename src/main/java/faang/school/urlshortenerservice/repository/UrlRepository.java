package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE hash IN (
                SELECT hash FROM url
                WHERE expiration_time < CURRENT_TIMESTAMP FOR UPDATE)
            RETURNING hash
            """)
    List<String> getOldUrlsAndDelete();
}

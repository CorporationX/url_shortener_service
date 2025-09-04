package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.api.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM url
                    WHERE created_at < :dateTime
                    AND hash IN (SELECT hash FROM url LIMIT :limit)
                    RETURNING hash
                    """
    )
    @Modifying
    List<String> deleteUrlBeforeCreatedAt(LocalDateTime dateTime, int limit);

    default Url findByIdOrThrow(String id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Url not found"));
    }
}

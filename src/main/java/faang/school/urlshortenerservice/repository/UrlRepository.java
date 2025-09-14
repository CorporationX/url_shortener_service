package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM url
                    WHERE created_at < :dateTime
                    RETURNING hash
                    """
    )
    @Modifying
    List<String> deleteUrlBeforeCreatedAt(Instant dateTime);

    default Url findByIdOrThrow(String id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Url not found"));
    }
}
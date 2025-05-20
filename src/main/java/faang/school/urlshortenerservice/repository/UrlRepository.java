package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < :cutoffDate RETURNING hash",
            nativeQuery = true)
    List<String> deleteByCreatedAtBefore(@Param("cutoffDate") Instant cutoffDate);

    Optional<Url> findByUrl(String url);
}

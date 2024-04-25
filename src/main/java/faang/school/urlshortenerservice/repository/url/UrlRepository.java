package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH deleted AS (
              DELETE FROM urls WHERE created_at < ?
              RETURNING hash
            )
            SELECT * FROM deleted;""")
    Set<String> deleteByDateAndGetHashes(LocalDateTime dateTime);
}

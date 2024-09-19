package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            WITH deleted AS (
                DELETE FROM url
                WHERE created_at < :dateExpired
                RETURNING hash
            )
            SELECT hash FROM deleted
            """)
    List<String> findAndDeleteHashExpired(LocalDateTime dateExpired);
}
package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {

    @Transactional
    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < :cutoffDate
        RETURNING hash
        """)
    List<String> deleteOldUrlsAndReturnHashes(LocalDateTime cutoffDate);
}

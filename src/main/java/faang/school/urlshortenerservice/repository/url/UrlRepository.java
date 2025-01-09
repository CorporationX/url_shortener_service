package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM url
    WHERE created_at < now() - ( :days || ' days' )::interval
    RETURNING hash
    """, nativeQuery = true)
    List<String> getAndDeleteUrlsByDate(@Param("days") int days);
}
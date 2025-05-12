package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(value = """
        DELETE FROM url
        WHERE created_at < :cutoff
        RETURNING hash
    """, nativeQuery = true)
    List<String> deleteOldUrlsBefore(@Param("cutoff") Timestamp cutoff);
}

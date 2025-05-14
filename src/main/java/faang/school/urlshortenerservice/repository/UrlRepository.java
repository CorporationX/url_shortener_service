package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, String> {

    @Modifying
    @Query(value = """
        DELETE FROM urlEntity
        WHERE created_at < :cutoff
        RETURNING hash
    """, nativeQuery = true)
    List<String> deleteOldUrlsBefore(@Param("cutoff") Timestamp cutoff);

    Optional<UrlEntity> findByUrl(String url);
}

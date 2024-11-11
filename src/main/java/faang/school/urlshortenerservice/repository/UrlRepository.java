package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    @Query(value = "DELETE FROM url WHERE created_at < :date RETURNING hash", nativeQuery = true)
    List<String> deleteAndReturnOldUrls(@Param("date") LocalDateTime date);
}

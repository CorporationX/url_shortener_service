package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Query("SELECT u FROM Url u WHERE u.createdAt < :cutoffDate")
    List<Url> findAllUrlsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<URL, Long> {
    Optional<URL> findByHash(String hash);

    @Query("SELECT u FROM URL u WHERE u.createdAt < :cutoffDate")
    java.util.List<URL> findOldURLs(LocalDateTime cutoffDate);

    @Query("SELECT u FROM URL u ORDER BY u.accessCount DESC LIMIT 100")
    java.util.List<URL> findMostPopularURLs();
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.ShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {
    @Query("SELECT s FROM ShortenedUrl s WHERE s.hash=:hash")
    Optional<ShortenedUrl> findByHash(@Param("hash") String actualHash);

    @Query("SELECT s FROM ShortenedUrl s WHERE s.createdAt < :cutoff")
    List<ShortenedUrl> findShortenedUrlsByCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}

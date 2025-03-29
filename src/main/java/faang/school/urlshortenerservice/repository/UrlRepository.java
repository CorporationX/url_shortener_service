package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(value = """
            DELETE FROM urls
            WHERE urls.created_at < :afterDeleteDate
            RETURNING *
            """, nativeQuery = true)
    List<Url> deleteOldUrls(LocalDateTime afterDeleteDate);
}

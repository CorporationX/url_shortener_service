package faang.school.urlshortenerservice.repository.url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    
    @Query("SELECT u.originalUrl FROM UrlEntity u WHERE u.hash = :hash")
    Optional<String> findUrlByHash(@Param("hash") String hash);

    @Modifying
    @Query("DELETE FROM UrlEntity u WHERE u.createdAt < :date RETURNING u.hash")
    List<String> deleteOldUrlsAndReturnHashes(LocalDateTime date);
}

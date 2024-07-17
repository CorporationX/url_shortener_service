package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
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
    boolean existsByUrl(String url);

    @Modifying
    @Query("DELETE FROM Url u WHERE u.createdAt < :oneYearAgo RETURNING u.hash")
    List<String> deleteExpiredUrlsAndReturnHashes(LocalDateTime oneYearAgo);
}

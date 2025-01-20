package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<String> findByHash(String hash);

    @Query(nativeQuery = true, value = "DELETE FROM Url u WHERE u.creationDate < :oneYearAgo RETURNING u.hash")
    List<String> deleteOldUrls(LocalDateTime oneYearAgo);

    Optional<Url> findByUrl(String longUrl);
}

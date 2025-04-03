package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    List<String> findHashesByUrl(@Param("url") String url);

    @Query("SELECT u FROM Url u WHERE u.expiredAt < :currentTime")
    List<Url> findAllExpiredUrls(@Param("currentTime") LocalDateTime currentTime);

    default Optional<String> findUrlByHash(String hash) {
        return findById(hash).map(Url::getUrl);
    }
}

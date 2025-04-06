package faang.school.urlshortenerservice.repository.Url;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < :yearAgo RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrls(LocalDateTime yearAgo);
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(value = "delete from url u where u.created_at < :period returning *", nativeQuery = true)
    List<Url> deleteOldUrl(LocalDateTime period);

    Optional<Url> findByBaseUrl(String baseUrl);
}
package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < :createdAt RETURNING hash", nativeQuery = true)
    List<String> findAndDeleteByCreatedAtBefore(@Param("createdAt") LocalDateTime createdAt);

    Optional<Url> getByHash(String hash);
}

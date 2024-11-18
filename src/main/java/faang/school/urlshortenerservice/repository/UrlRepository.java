package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM urls WHERE created_at < :dateTime RETURNING hash")
    List<String> removeOldLinks(@Param("dateTime") LocalDateTime dateTime);
}

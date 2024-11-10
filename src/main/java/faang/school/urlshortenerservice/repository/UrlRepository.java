package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByHash(String hash);

    @Query("SELECT u FROM Url u WHERE u.createdAt < :dateThreshold")
    List<Url> findUrlsOlderThanOneYear(@Param("dateThreshold") LocalDateTime dateThreshold);

}

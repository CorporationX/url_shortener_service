package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByHash(String hash);

    @Modifying
    @Query(value = "DELETE FROM urls u WHERE u.created_at < :thresholdDate RETURNING u.hash", nativeQuery = true)
    List<String> deleteOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);
}

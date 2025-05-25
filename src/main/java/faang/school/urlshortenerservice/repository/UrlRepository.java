package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < :threshold RETURNING hash", nativeQuery = true)
    List<String> deleteByCreatedAtBeforeReturningHashes(@Param("threshold") Instant threshold);
}

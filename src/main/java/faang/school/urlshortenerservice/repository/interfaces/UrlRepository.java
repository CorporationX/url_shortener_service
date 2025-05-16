package faang.school.urlshortenerservice.repository.interfaces;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(value = "DELETE FROM url WHERE created_at < :threshold RETURNING hashes", nativeQuery = true)
    List<String> deleteOlderThan(LocalDateTime threshold);
}

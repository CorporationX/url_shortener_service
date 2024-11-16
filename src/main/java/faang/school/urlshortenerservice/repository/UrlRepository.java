package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = "SELECT hash FROM url WHERE created_at < :cutoffDate")
    List<String> findExpiredUrls(LocalDateTime cutoffDate);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year'")
    void deleteExpiredUrls();

}

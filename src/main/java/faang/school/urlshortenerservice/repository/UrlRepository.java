package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(nativeQuery = true, value ="""
            DELETE FROM url u WHERE u.created_at < ?1 RETURNING *
            """)
    @Modifying
    List<Url> cleanOldUrl(LocalDateTime minusDays);

    Optional<Url> findByHash(String hash);
}

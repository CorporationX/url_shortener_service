package faang.school.urlshortenerservice.repository.api;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
        SELECT u.hash FROM Url u WHERE u.url = :url
    """)
    Optional<String> findHashByUrl(@Param("url") String url);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url WHERE created_at <= :hashDeadline
        RETURNING *
    """)
    List<Hash> cleaningExpiredUrls(@Param("hashDeadline") LocalDateTime deadline);
}

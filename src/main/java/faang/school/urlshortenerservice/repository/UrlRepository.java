package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findUrlByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url u
        WHERE u.hash IN (
            SELECT u_inner.hash
            FROM url u_inner
            WHERE u_inner.created_at < :time
            FOR UPDATE SKIP LOCKED
        )
        RETURNING u.url
        """)
    List<Url> getAndDeleteOldUrls(LocalDateTime time);
}

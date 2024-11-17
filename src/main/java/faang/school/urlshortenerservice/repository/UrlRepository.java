package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL :period
            RETURNING hash
            """)
    Optional<List<String>> getHashAndDeleteURL(@Param("period") String period);

    @Query(nativeQuery = true, value = """
            SELECT * FROM url u
            WHERE u.hash = :hash
            """)
    Optional<Url> findUrlByHash(@Param("hash") String hash);

    @Query(nativeQuery = true, value = """
            SELECT u.hash FROM url u
            WHERE u.url = :url
            """)
    Optional<String> findHashByUrl(@Param("url") String url);
}

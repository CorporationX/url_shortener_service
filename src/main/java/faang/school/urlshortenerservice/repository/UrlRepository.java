package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.URL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<URL, Long> {
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE created_at < NOW() - INTERVAL :period
            RETURNING hash
            """)
    Optional<List<String>> getHashAndDeleteURL(@Param("period") String period);

    @Query(nativeQuery = true, value = """
            SELECT u.url FROM url u
            WHERE u.hash = :hash
            """)
    Optional<String> findUrlByHash(@Param("hash") String hash);

    @Query(nativeQuery = true, value = """
            SELECT u.hash FROM url u
            WHERE u.url = :url
            """)
    Optional<String> findHashByUrl(@Param("url") String url);
}
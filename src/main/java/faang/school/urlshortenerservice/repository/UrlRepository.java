package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    Optional<String> findHashByUrl(@Param("url") String url);

    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    Optional<String> findUrlByHash(@Param("hash") String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < :date
        RETURNING hash;
    """)
    List<String> deleteAndGetOldUrls(@Param("date") LocalDate date);
}

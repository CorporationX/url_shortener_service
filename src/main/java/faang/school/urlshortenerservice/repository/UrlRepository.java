package faang.school.urlshortenerservice.repository;

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

    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    Optional<String> findUrlByHash(@Param("hash") String hash);

    @Modifying
    @Query(value = """
        DELETE FROM url 
        WHERE created_at < :dateTime
        RETURNING hash, created_at
        """, nativeQuery = true)
    List<Hash> deleteOldUrlsAndReturnHashes(LocalDateTime dateTime);
}

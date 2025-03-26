package faang.school.urlshortenerservice.repository;

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

    Optional<Url> findByHash(String hash);

    Optional<Url> findByUrl(String url);

    @Modifying
    @Query(value = """
            WITH selected_hashes AS (
                SELECT hash
                FROM hash
                ORDER BY RANDOM()
                LIMIT ?
                FOR UPDATE SKIP LOCKED
            )
            DELETE FROM hash
            USING selected_hashes
            WHERE hash.hash = selected_hashes.hash
            RETURNING hash.hash
            """, nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes(@Param("date") LocalDateTime date);
}


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
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("SELECT url FROM Url WHERE hash = :hash")
    Optional<String> findUrlByHash(String hash);

    @Query(value = """
            SELECT hash
            FROM url
            WHERE created_at < :dateTime
            ORDER BY created_at
            LIMIT :limit
            FOR UPDATE
            """, nativeQuery = true)
    List<String> findHashesToDelete(LocalDateTime dateTime, int limit);

    @Modifying
    @Query(value = """
            DELETE FROM url
            WHERE hash IN :hashes
            """, nativeQuery = true)
    void deleteByHashes(List<String> hashes);
}
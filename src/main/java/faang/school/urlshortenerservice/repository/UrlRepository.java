package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Hash, String> {

    @Query(nativeQuery = true, value = """
            INSERT INTO url (hash, url, created_at) VALUES (:hash, :url, :created_at)
            """)
    @Modifying
    void save(String hash, String url, LocalDateTime created_at);

    @Query(nativeQuery = true, value = """
            SELECT u.url FROM url u
            WHERE u.hash = :hash
            """)
    @Modifying
    Url getLongUrl(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at IN (
            SELECT hash FROM url WHERE created_at < :oneYearAgo
            ) RETURNING hash
            """)
    List<Hash> findAndDelete(LocalDateTime oneYearAgo);
}

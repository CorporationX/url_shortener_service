package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
                DELETE FROM url u
                WHERE u.created_at < :date
                RETURNING u.hash
            """)
    List<Hash> deleteOldUrlsAndGetFreedHashes(LocalDateTime date);
}
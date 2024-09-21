package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE
            FROM url
            WHERE DATE_PART('day', NOW() - created_at) > :expirationDays
            RETURNING hash
            """)
    List<String> deleteAndGetExpiredHashes(int expirationDays);
}

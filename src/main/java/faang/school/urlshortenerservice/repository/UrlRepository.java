package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE created_at <= current_date - INTERVAL '1 year'
            RETURNING hash
            """)
    List<Hash> deleteOldHashes();
}

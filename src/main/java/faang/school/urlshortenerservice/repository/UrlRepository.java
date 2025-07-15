package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
            "DELETE FROM url WHERE created_at < DATE('now', '-1 year')
            RETURNING hashes"
            """)
    List<String> deleteUrlsOlderOneYearAndSaveByHash();

}

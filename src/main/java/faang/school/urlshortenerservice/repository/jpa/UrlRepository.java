package faang.school.urlshortenerservice.repository.jpa;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM url
            WHERE url.created_at < :from
            RETURNING hash
            """)
    List<String> removeOldAndGetHashes(LocalDateTime from);
}

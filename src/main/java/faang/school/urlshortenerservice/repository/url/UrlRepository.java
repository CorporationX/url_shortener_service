package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
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
            WHERE created_at < :date
            RETURNING hash
            """)
    List<String> getAndDeleteUrlsByDate(@Param("date") LocalDateTime date);
}

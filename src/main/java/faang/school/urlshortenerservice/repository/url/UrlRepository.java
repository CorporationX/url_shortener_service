package faang.school.urlshortenerservice.repository.url;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(value = """
                    DELETE FROM hash 
                    WHERE hash NOT IN (SELECT hash FROM url WHERE hash IS NOT NULL)
                    LIMIT ?;
            """, nativeQuery = true)
    List<String> deleteOldUrls();
}

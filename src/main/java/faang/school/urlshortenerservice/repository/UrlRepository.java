package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByHash(@Param("hash") String hash);

    Optional<Url> findByUrl(@Param("url") URL url);

    @Query(nativeQuery = true, value = """
             DELETE FROM url
             WHERE created_at < NOW() - INTERVAL '1 year'
             RETURNING * 
            """)
    List<Url> findAndRemoveAllOldEntity();
}

package faang.school.urlshortenerservice.repozitory;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
        SELECT hash FROM url WHERE url = :url
        """)
    String returnHashByUrlIfExists(String url);

    @Query(nativeQuery = true, value = """
        SELECT url FROM url WHERE hash = :hash
        """)
    String findUrlByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' 
        RETURNING hash
        """)
    List<String> deleteOldUrlsAndReturnHashes();
}

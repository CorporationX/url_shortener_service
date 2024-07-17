package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT hash FROM url u WHERE u.url = :url
            """)
    String getHash(String url);

    @Query(nativeQuery = true, value = """
            SELECT url FROM url u WHERE u.hash = :hash
            """)
    String getUrl(String hash);

    @Query(nativeQuery = true, value = """
            DELETE FROM url u WHERE u.created_at < NOW() - INTERVAL '1 year' RETURNING u.hash  
            """)
    List<String> deleteAndGetOldHashes();
}

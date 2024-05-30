package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Url findByHash(String hash);
    @Modifying
    @Query(value = """
           WITH old_urls AS (
               SELECT hash
               FROM url
               WHERE created_at < current_timestamp - INTERVAL '1 year'
           )
           DELETE FROM url
           USING old_urls
           WHERE url.hash = old_urls.hash
           RETURNING old_urls.hash;
           """, nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes();
}

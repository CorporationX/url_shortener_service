package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends CrudRepository<Url, Long> {
    @Query(nativeQuery = true, value = """
                select hash from url where url = :url
            """)
    String getHashByUrl(String url);

    @Query(nativeQuery = true, value = """
                select url from url where hash = :hash
            """)
    String getLongUrlByHash(String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
                    WITH deleted_urls AS (
                            DELETE FROM url WHERE created_at <= NOW() - INTERVAL '1 MINUTE' RETURNING hash
                )
                    INSERT INTO free_hash_pool (hash)
                    SELECT hash
                    FROM deleted_urls
            """)
    void removeOldUrlsToHash();
}

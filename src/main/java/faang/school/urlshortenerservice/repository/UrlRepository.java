package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
                SELECT url FROM url WHERE hash = :hash
            """)
    String getUrlByHash(@Param("hash") String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
                    DELETE FROM url WHERE delete_at <= now() RETURNING hash;
            """)
    List<String> deleteOldUrls();

    @Modifying
    @Query(nativeQuery = true, value = """
                INSERT INTO url VALUES (:#{#url.hash}, :#{#url.url}, :#{#url.createdAt}, :#{#url.deletedAt})
            """)
    void insert(@Param("url") Url url);
}

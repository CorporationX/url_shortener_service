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

    @Query(value = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash", nativeQuery = true)
    @Modifying
    List<String> removeOldLinksAndReturnHash();

    @Query(value = "INSERT INTO url VALUES (:hash, :url)", nativeQuery = true)
    @Modifying
    void saveUrlWithNewHash(@Param("hash") String hash, @Param("url") String url);

    @Query(value = "SELECT url FROM url WHERE hash = :hash", nativeQuery = true)
    String getUrlByHash(@Param("hash") String hash);
}

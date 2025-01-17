package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    String getByUrl(@Param("url") String url);

    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    String getByHash(@Param("hash") String hash);

    @Modifying
    @Query(nativeQuery = true, value = """
                        DELETE FROM url WHERE url.created_at < current_timestamp - interval '1 year'
                        RETURNING hash
            """ )
    List<String> getAndDeleteExpiredData();
}

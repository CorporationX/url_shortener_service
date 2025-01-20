package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = "SELECT u.hash FROM url u WHERE u.url = :url")
    String findHashByUrl(String url);

    @Query(nativeQuery = true, value = """
        DELETE FROM url
        WHERE created_at < :cutoff
        RETURNING hash
        """)
    List<String> deleteUrlsAndReturnHashList(LocalDateTime cutoff);

}

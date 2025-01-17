package faang.school.urlshortenerservice.repository.jpa;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    @Query(nativeQuery = true, value = """
            DELETE FROM url WHERE expires_at < NOW()
            RETURNING hash
            """)
    List<String> getExpiredHashAndDeleteUrl();
}
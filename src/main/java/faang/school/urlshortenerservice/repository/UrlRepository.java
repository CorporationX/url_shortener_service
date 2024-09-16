package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {

    Optional<Url> findByUrl(String url);

    @Query(nativeQuery = true, value = """
            DELETE FROM url u
                   WHERE u.created_at <= :before
            returning u.hash;
            """)
    List<String> pollBefore(LocalDateTime before);
}

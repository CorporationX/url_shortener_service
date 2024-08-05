package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    @Modifying
    @Query(nativeQuery = true, value = """
               DELETE FROM url as u WHERE u.created_at < now() - INTERVAL '1 year' returning u.hash
            """)
    List<String> removeUrlOlderThanOneYear();

    Optional<Url> findByUrl(String url);
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT hash FROM url u WHERE u.url = :url
            """)
    String getHash(String url);
}

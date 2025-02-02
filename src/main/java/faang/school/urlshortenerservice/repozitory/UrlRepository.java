package faang.school.urlshortenerservice.repozitory;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(nativeQuery = true, value = """
            SELECT u.hash FROM Url u WHERE u.url = :url
            """)
    String returnHashForUrlIfExists(String url);

}

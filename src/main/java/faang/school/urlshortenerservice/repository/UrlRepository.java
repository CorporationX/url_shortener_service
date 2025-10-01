package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    Boolean existsUrlByUrl(String url);
    Url findByUrl(String url);
    Url findByHash(String hash);

    @Query(value = "SELECT u FROM Url u WHERE u.createdAt <= CURRENT_DATE - 365")
    List<Url> findOldUrls();
}

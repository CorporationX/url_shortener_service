package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShortUrlRepository extends JpaRepository<Url, String> {

    @Query("SELECT u.originalUrl FROM Url u WHERE u.shortUrl = :shortUrl")
    String findOriginalUrlByShortUrl(String shortUrl);
}

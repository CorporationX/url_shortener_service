package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(value = "DELETE FROM Url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING url.hash;",
            nativeQuery = true)
    List<Hash> deleteOlderOneYearUrls();
}

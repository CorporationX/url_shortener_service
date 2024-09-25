package faang.school.urlshortenerservice.repository;


import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    Optional<String> findUrlByHash(String hash);

    @Query(value = "DELETE FROM url WHERE created_at < now() - interval '3 hours' RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes();
}

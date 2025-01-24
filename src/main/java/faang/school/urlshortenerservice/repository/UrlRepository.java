package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Url WHERE created_at < now() - interval '1 year' RETURNING hash", nativeQuery = true)
    List<String> deleteOldUrlsAndReturnHashes();

    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    String findUrlByHash(String hash);

    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    String returnHashForUrlIfExists(String url);
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query("SELECT u.hash FROM Url u WHERE u.url = :url")
    String getByUrl(@Param("url") String url);

    @Query("SELECT u.url FROM Url u WHERE u.hash = :hash")
    String getByHash(@Param("hash") String hash);
}

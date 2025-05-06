package faang.school.urlshortenerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query(value = "SELECT url FROM url WHERE hash = :hash", nativeQuery = true)
    String getUrlByHash(@Param("hash") String hash);
}

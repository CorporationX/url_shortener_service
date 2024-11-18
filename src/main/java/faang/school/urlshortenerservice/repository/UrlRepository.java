package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    boolean existsByUrl(String url);
    Optional<Url> findByHash(String hash);

    Optional<Url> findByUrl(String url);

    @Query(value = "delete from url where created_at < now() - interval :interval returning url.hash;",
            nativeQuery = true)
    List<String> getAndDeleteAllUrlsOlderInterval(@Param("interval") String interval);


}

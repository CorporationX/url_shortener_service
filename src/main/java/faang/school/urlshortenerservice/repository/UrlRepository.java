package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Query("SELECT u.url FROM url u WHERE u.hash = :hash")
    Optional<String> findUrlByHash(@Param("hash") String hash);
}

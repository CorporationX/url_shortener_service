package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(nativeQuery = true, value = "SELECT hash from url where hash = ?")
    Optional<String> findHashByHash(String hash);
}

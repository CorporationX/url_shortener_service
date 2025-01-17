package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.URL;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByHash(@Param("hash") String hash);

    Optional<Url> findByUrl(@Param("url") URL url);
}

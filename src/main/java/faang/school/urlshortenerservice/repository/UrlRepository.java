package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, String> {
    Url findByHash(@Param("hash") String hash);
}

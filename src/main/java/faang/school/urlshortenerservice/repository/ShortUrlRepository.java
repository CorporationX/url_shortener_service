package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, String> {

}

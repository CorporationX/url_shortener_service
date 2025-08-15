package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.document.ShortUrl;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortUrlRepository extends MongoRepository<ShortUrl, Long> {
    boolean existsByCode(String code);

    Optional<ShortUrl> findByCode(String code);

    default ShortUrl findByCodeOrThrow(String code) {
        return findByCode(code).orElseThrow(() -> new EntityNotFoundException("Url not found, short code: " + code));
    }
}

package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Repository
@RequiredArgsConstructor
public class UrlRepository {

    @Value("${url.days-to-live}")
    private int daysToLive;

    private final UrlJpaRepository repository;

    public Optional<Url> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    public Url findByHash(String hash) {
        return repository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Not found original url by passed short url "));
    }

    public List<Url> findOldUrls() {
        return repository.findUrlsOlderThan(now().minusDays(daysToLive));
    }

    public Url save(Url url) {
        return repository.save(url);
    }
}

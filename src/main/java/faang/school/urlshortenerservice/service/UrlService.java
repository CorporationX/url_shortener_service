package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

    @Value("${url.days-to-live}")
    private int daysToLive;

    @Transactional(readOnly = true)
    public Optional<Url> findByUrl(String url) {
        return urlRepository.findByUrl(url);
    }

    @Transactional(readOnly = true)
    public Url findByHash(String hash) {
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new DataValidationException("Not found original url by passed short url "));
    }

    @Transactional(readOnly = true)
    public List<Url> findOldUrls() {
        return urlRepository.findOlderUrls(now().minusDays(daysToLive));
    }

    public void saveUrl(Url url) {
        urlRepository.save(url);
    }
}

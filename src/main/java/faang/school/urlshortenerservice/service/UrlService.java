package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "urlCache", key = "#hash")
    public String getLongUrlByHash(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException(Url.class, hash));

        return url.getUrl();
    }
}
package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Override
    public String getOriginalUrl(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }

        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash));

        urlCacheRepository.save(hash, url.getUrl());

        return url.getUrl();
    }
}

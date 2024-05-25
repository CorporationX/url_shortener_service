package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public UrlDto convertToShortUrl(UrlDto urlDto) { //TODO: доделать
        return null;
    }

    public String redirectOriginalUrl(String hash) {
        return urlCacheRepository.getByHash(hash).orElseGet(
                () -> urlRepository.getByHash(hash).map(Url::getBaseUrl).orElseThrow(
                        () -> new EntityNotFoundException("URL not found for hash: " + hash)));
    }
}

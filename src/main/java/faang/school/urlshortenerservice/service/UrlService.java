package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    @Value("${url.cache.initial.ttl}")
    private Duration INITIAL_TIMEOUT;

    @Value("${url.cache.secondary.ttl}")
    private Duration SECONDARY_TIMEOUT;

    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        Url redirectUrl = urlMapper.toEntity(urlDto);
        String hash = hashCache.getHash();
        redirectUrl.setHash(hash);
        Url savedUrl = urlRepository.save(redirectUrl);
        urlCacheRepository.save(savedUrl.getHash(), savedUrl.getUrl(), INITIAL_TIMEOUT);
        return urlMapper.toDto(savedUrl);
    }

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.get(hash);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        } else {
            Optional<Url> urlOptional = urlRepository.findByHash(hash);
            if (urlOptional.isPresent()) {
                String url = urlOptional.get().getUrl();
                urlCacheRepository.save(hash, url, SECONDARY_TIMEOUT);
                return url;
            } else {
                throw new EntityNotFoundException("Could not find this redirecting url");
            }
        }
    }
}

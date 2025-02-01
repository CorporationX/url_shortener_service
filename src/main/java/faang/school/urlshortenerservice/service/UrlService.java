package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public UrlResponseDto processUrl(String url) {
        if (urlRepository.existsByUrl(url)) {
            throw new EntityExistsException("Url already exists: " + url);
        }
        String hash = hashCache.getHash().getHash();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(url)
                .createdAt(LocalDateTime.now()).build();

        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash, url);
        return urlMapper.toDto(urlEntity);
    }

    public String getUrlByHash(String hash) {
        Optional<String> cachedUrl = urlCacheRepository.findByHash(hash);
        return cachedUrl.orElseGet(() ->
                urlRepository.findByHash(hash)
                        .orElseThrow(() -> new EntityNotFoundException("URL not found for hash: " + hash)));
    }
}
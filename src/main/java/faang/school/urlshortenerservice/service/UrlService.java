package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlMapper urlMapper;
    @Value("${url.name-pattern}")
    private String urlPattern;

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        String hashExist = getHashIfExistUrl(urlDto.getUrl());
        if (hashExist != null) {
            return new UrlDto(urlPattern + hashExist);
        }

        String hash = hashCache.getHash();
        String shortUrl = urlPattern + hash;
        Url url = new Url();
        url.setUrl(urlDto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);
        urlCacheRepository.save(urlMapper.toUrlCash(url));

        return new UrlDto(shortUrl);
    }

    private String getHashIfExistUrl(String urlInput) {
        UrlCache urlCache = urlCacheRepository.findByUrl(urlInput);
        if (urlCache != null) {
            return urlCache.getHash();
        }
        Url url = urlRepository.findUrlByUrl(urlInput);
        if (url != null) {
            urlCacheRepository.save(urlMapper.toUrlCash(url));
            return url.getHash();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {

        Optional<UrlCache> urlCash = urlCacheRepository.findById(hash);
        if (urlCash.isPresent()) {
            return urlCash.get().getUrl();
        }
        String url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Error or outdated parameter"))
                .getUrl();
        urlCacheRepository.save(new UrlCache(hash, url));
        return url;
    }

    @Transactional
    public void cleanHash() {
        log.info("Started clearing URL records");
        List<String> freeHashes = urlRepository.deleteOldUrl();
        List<Hash> hashes = freeHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
        log.info("Count cleared old URL records - {}", freeHashes.size());
    }
}
package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.hash.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "urlCache")
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final HashRepository hashRepository;

    @Value("${app.scheduler.clean-unused.period}")
    private Period removeUnusedPeriod;

    @Override
    @Cacheable(key = "#hash")
    public String findOriginalUrl(String hash) {
        Url foundUrl = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException("Original url for hash %s was not found".formatted(hash)));
        return foundUrl.getUrl();
    }

    @Override
    @CachePut(key = "#result.url.substring(#result.url.lastIndexOf('/') + 1)", value = "#urlDto.url")
    @Transactional
    public UrlDto getShortUrl(UrlDto urlDto, HttpServletRequest httpServletRequest) {
        String cachedHash = hashCache.getHash();
        Url urlToSave = urlMapper.toUrlModel(urlDto);
        urlToSave.setHash(cachedHash);
        urlRepository.save(urlToSave);
        String requestBaseUrl = httpServletRequest.getRequestURL().toString();
        return new UrlDto(String.format("%s/%s", requestBaseUrl, cachedHash));
    }

    @Override
    @Transactional
    public void removeUnusedUrls() {
        List<Url> oldUrls = urlRepository.findAllUrlsOlderThan(LocalDateTime.now().minus(removeUnusedPeriod));
        urlRepository.deleteAll(oldUrls);
        List<Hash> hashes = oldUrls.stream()
                .map(oldUrl -> new Hash(oldUrl.getHash()))
                .toList();
        evictUrlsFromCache(hashes);
        hashRepository.saveAll(hashes);
    }

    @CacheEvict(key = "#hashes.![hash]")
    public void evictUrlsFromCache(List<Hash> hashes) {
    }
}
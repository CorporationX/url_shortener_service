package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.entity.UrlHash;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlShorterService {
    private final HashCache urlHashCache;
    private final UrlCache urlCache;
    private final UrlRepository urlRepository;
    private final UrlHashRepository urlHashRepository;

    @Transactional
    public ShortUrlDto shortenUrl(String longUrl) {
        String hash = urlHashCache.getHash();
        Url url = Url.builder().url(longUrl).shortUrl(hash).build();

        Url savedUrl = urlRepository.save(url);

        urlCache.saveUrl(hash, savedUrl.getUrl());

        return new ShortUrlDto(savedUrl.getId(), savedUrl.getShortUrl());
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortUrl) {
        return urlCache.getUrl(shortUrl).orElseThrow(
                () -> new UrlNotFoundException(shortUrl)
        );
    }

    @Transactional
    public void updateUrl(@NotNull Long urlId, String url) {
        Url u = urlRepository.findById(urlId).orElseThrow(
                () -> new UrlNotExistException(urlId)
        );
        u.setUrl(url);
        u.setUpdatedAt(LocalDateTime.now());
        urlCache.saveUrl(u.getShortUrl(), u.getUrl());
    }

    @Transactional
    public void deleteOldULRs(LocalDate obsolescenceDate) {
        List<Url> outdatedUrls = urlRepository.popAllOldUrl(obsolescenceDate);
        urlHashRepository.saveAll(
                outdatedUrls.stream()
                        .map(u -> new UrlHash(u.getShortUrl()))
                        .toList()
        );
    }
}

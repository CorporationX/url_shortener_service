package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Transactional
    public String shortenUrl(UrlDto url) {
        Url shortUrl = Url.builder()
                    .url(url.getUrl())
                    .hash(hashCache.getHash())
                    .lastReceivedAt(LocalDateTime.now())
                    .build();
        shortUrl = urlRepository.save(shortUrl);
        urlCacheRepository.save(new Hash(shortUrl.getHash()), shortUrl);
        return String.valueOf(shortUrl.getHash());
    }

    @Transactional(readOnly = true)
    public Url getLongUrl(String hash) {
        Optional<Url> url = urlCacheRepository.findByHash(new Hash(hash));
        return url.orElseGet(() -> urlRepository.findByHash(hash).
                orElseThrow(() -> new EntityNotFoundException("no url with such hash: " + hash)));
    }
}

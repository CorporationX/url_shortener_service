package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private static final String URL_KEY = "https://faang.school/";

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlRedisRepository urlRedisRepository;

    @Transactional
    @Override
    public UrlDto shortenUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = new Url(hash, urlDto.getUrl(), LocalDateTime.now());
        urlRepository.save(url);
        urlRedisRepository.save(hash, url);
        String shortUrl = URL_KEY + hash;
        return new UrlDto(shortUrl);
    }

    @Transactional(readOnly = true)
    @Override
    public UrlDto getNormalUrl(String hash) {
        Optional<Url> urlFromRedis = urlRedisRepository.find(hash);
        if (urlFromRedis.isPresent()) {
            return new UrlDto(urlFromRedis.get().getUrl());
        } else {
            return urlRepository.findByHash(hash)
                    .map(url -> new UrlDto(url.getUrl()))
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Url with %s hash not found", hash)));
        }
    }
}

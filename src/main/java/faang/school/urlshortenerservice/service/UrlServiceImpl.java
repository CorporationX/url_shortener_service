package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService{

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public UrlDto shortenUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, url);
        urlDto.setUrl(String.format("https://urlshortener/%s", hash));
        return urlDto;
    }

    public Optional<Url> getUrl(String hash) {
        return Optional.ofNullable(urlCacheRepository.getUrl(hash)
                .or(() -> urlRepository.findById(hash))
                .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash)));
    }

}

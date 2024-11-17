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

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService{

    private static final String SHORT_URL = "https://urlshortener/";

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    @Override
    public UrlDto toShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url urlEntity = Url.builder()
                .hash(hash)
                .url(urlDto.getUrl())
                .build();
        urlRepository.save(urlEntity);
        urlCacheRepository.save(hash, urlEntity);
        urlDto.setUrl(String.format(SHORT_URL + hash));
        return urlDto;
    }

    @Override
    public Url getUrl(String hash) {
        Url urlEntity = urlCacheRepository.getUrl(hash);
        if (urlEntity == null) {
            urlEntity = urlRepository.findById(hash)
                    .orElseThrow(() -> new UrlNotFoundException("Url not found for hash: " + hash));
        }
        return urlEntity;
    }

}

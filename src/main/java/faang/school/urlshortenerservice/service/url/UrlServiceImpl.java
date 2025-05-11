package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NoAvailableHashInCacheException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.hash.HashCache;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto urlRequestDto) {
        String originalUrl = urlRequestDto.getUrl();
        String hash = hashCache.getHash();
        if (hash == null) {
            throw new NoAvailableHashInCacheException("No available hash in cache now");
        }
        hashRepository.save(Hash.builder().hash(hash).build());
        Url url = Url.builder().hash(hash).url(originalUrl).build();
        urlRepository.save(url);
        urlCacheRepository.save(hash, originalUrl);
        return UrlResponseDto.builder().url(url.getUrl()).hash(hash).build();
    }

    @Override
    @Transactional
    public UrlResponseDto getOriginalUrl(String hash) {
        String url = urlCacheRepository.get(hash);
        if (url == null) {
            url = urlRepository.findById(hash)
                    .map(Url::getUrl)
                    .orElseThrow(() -> {
                        String message = String.format("URL not found for hash: %s", hash);
                        return new UrlNotFoundException(message);
                    });
            urlCacheRepository.save(hash, url);
        }
        return UrlResponseDto.builder().url(url).hash(hash).build();
    }
}

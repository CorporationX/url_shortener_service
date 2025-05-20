package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    private static final String BASE_URL = "https://url-shortener.faang.org/";

    @Transactional
    public UrlDto shortenUrl(UrlDto urlDto) {
        String hash = hashCache.getHash().join();

        Url url = Url.builder()
                .url(urlDto.url())
                .hash(hash)
                .createdAt(LocalDateTime.now())
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(hash, urlDto.url());
        return UrlDto.builder().url(BASE_URL + hash).build();
    }
}

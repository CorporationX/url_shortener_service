package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache cache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository cacheRepository;

    public String shorten(UrlRequestDto requestDto) {
        Url shortenUrl = new Url();
        String hash = cache.getHash();
        String url = requestDto.getUrl();

        shortenUrl.setUrl(url);
        shortenUrl.setHash(hash);
        shortenUrl.setCreatedAt(LocalDateTime.now());

        urlRepository.save(shortenUrl);
        cacheRepository.save(hash, url);

        return hash;
    }
}

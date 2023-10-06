package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;

    @Transactional
    public void getShortUrl(UrlDto urlDto) {
        String url = urlDto.getUrl();
        String hash = hashCache.getHash(url);

        urlCacheRepository.saveUrlWithHash(url, hash);
        urlRepository.saveUrlWithHash(url, hash);
    }
}

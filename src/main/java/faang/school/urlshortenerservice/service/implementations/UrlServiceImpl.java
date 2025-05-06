package faang.school.urlshortenerservice.service.implementations;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.interfaces.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    @Value("${url.original-path}")
    private String urlPath;

    @Override
    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlRepository.saveUrlWithNewHash(hash, urlDto.url());
        urlCacheRepository.save(hash, urlDto.url());
        return urlPath.concat(hash);
    }
}

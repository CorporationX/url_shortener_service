package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${hash.cache.ttl_days}")
    private int ttl;

    @Transactional
    public UrlDto createShortUrl(UrlDto url) {
        url.setHash(hashCache.getHash());
        Url savedUrl = urlRepository.save(urlMapper.toUrl(url));
        UrlDto savedUrlDto = urlMapper.toUrlDto(savedUrl);
        urlCacheRepository.set(savedUrl.getHash(), savedUrlDto, ttl);

        return savedUrlDto;
    }

    @Override
    public UrlDto getUrl(String hash) {
        UrlDto urlDto = urlCacheRepository.get(hash);
        if (urlDto == null) {
            return urlMapper.toUrlDto(urlRepository.findById(hash).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Url with hash [%s] not found", hash))));
        }

        return urlDto;
    }

    @Override
    public List<String> retrieveOldUrls(int daysCount) {
        return urlRepository.retrieveOldUrls(daysCount);
    }
}

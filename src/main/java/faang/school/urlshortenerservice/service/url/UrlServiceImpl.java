package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final RedisCacheRepository redisCacheRepository;
    @Value("${url.original-path}")
    private String urlPath;

    @Override
    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        urlRepository.saveUrlWithNewHash(hash, urlDto.url());
        redisCacheRepository.save(hash, urlDto.url());
        return urlPath.concat(hash);
    }

    @Override
    @Transactional
    public String getOriginalUrl(String hash) {
        String cachedUrl = redisCacheRepository.get(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        try {
            cachedUrl = urlRepository.getUrlByHash(hash);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Url with hash %s was not found in database".formatted(hash));
        }

        return cachedUrl;
    }
}

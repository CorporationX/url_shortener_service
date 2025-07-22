package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    @Value("${redis.ttl.seconds:3600}")
    private final long redisTtlSeconds;

    private final RedisTemplate<String, String> redisTemplate;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    @Override
    @Transactional
    public ShortUrlDto createShortUrl(String longUrl, HttpServletRequest request) {
        String hash = hashCache.getHash().getHash();
        Url url = new Url(hash, longUrl);
        urlRepository.save(url);
        redisTemplate.opsForValue().set(hash, longUrl, redisTtlSeconds, TimeUnit.SECONDS);
        String baseUrl = getBaseUrl(request);
        String shortUrl = baseUrl + "/redirect/" + hash;
        log.info("Short URL created: {} -> {}", hash, longUrl);
        return new ShortUrlDto(shortUrl);
    }


    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        return scheme + "://" + serverName + serverPort + contextPath + servletPath;
    }

    @Override
    public String getLongUrl(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url != null) {
            return url;
        }

        Optional<Url> urlOptional = urlRepository.findByHash(hash);
        if (urlOptional.isPresent()) {
            url = urlOptional.get().getUrl();
            return url;
        }

        throw new EntityNotFoundException("URL not found for hash: " + hash);
    }

    @Override
    public void deleteShortUrl(String shortUrl) {
        //todo очистка из базы и кеша по расписанию и по требованию?
    }
}

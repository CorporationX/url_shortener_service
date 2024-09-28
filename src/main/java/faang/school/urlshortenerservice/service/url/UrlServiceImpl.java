package faang.school.urlshortenerservice.service.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.hash.cache.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlMapper urlMapper;
    private final UrlValidator validator;
    private final HashCache hashCache;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${cleaner.url-expired-time-years}")
    private Long expiredTimeInYears;

    @Override
    public String getUrl(String hash) throws JsonProcessingException {
        validator.validateHash(hash);
        Url url;
        String longUrl = redisTemplate.opsForValue().get(hash);
        if (longUrl != null) {
            url = objectMapper.readValue(longUrl, Url.class);
        } else {
            url = urlRepository.findByHash(hash).orElseThrow(() -> new UrlNotExistException("Url doesn't exist"));
        }
        return url.getUrl();
    }

    @Override
    public UrlDto createShortUrl(String url) throws JsonProcessingException {
        validator.validateUrl(url);
        String hash = hashCache.getNextUniqueHash();
        Url saved = urlRepository.save(Url.builder()
                .hash(hash)
                .url(url)
                .build());
        redisTemplate.opsForValue().set(hash, objectMapper.writeValueAsString(saved));
        return urlMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void cleanOldUrls() {
        Set<String> hashes = urlRepository.deleteByDateAndGetHashes(LocalDateTime.now().minusYears(expiredTimeInYears));
        hashRepository.save(hashes);
    }

}

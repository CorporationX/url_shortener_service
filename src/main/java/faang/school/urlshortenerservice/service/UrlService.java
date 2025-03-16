package faang.school.urlshortenerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisService redisService;
    private final HashCache hashCache;

    @Transactional
    public String shortenUrl(String originalUrl) {
        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .build();

        Url saved = urlRepository.save(url);
        redisService.setValue(hash, originalUrl);
        log.info("Saved new url object: {}", url);
        return hash;
    }

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {
        String value = redisService.getValue(hash);
        if (value == null) {
            Url found = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new HashNotFoundException("Hash: " + hash + " is not exists in database"));
            redisService.setValue(hash, found.getUrl());
            return found.getUrl();
        }
        return value;
    }

}

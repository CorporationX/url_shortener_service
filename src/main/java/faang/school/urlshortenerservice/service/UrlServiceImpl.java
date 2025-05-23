package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import faang.school.urlshortenerservice.hash_generator.HashCache;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UrlRedisRepository urlRedisRepository;
    private final UrlMapper urlMapper;
    private final HashCache hashCache;

    @Value("${url-service.base-address:https://sh.com/}")
    @SuppressWarnings("unused")
    private String baseAddress;

    public String getShortUrl(@RequestBody ShortUrlRequestDto requestDto) {
        var hash = hashCache.getHash();
        var resultUrl = baseAddress + hash;

        var urlEntity = Url.builder().url(requestDto.url()).hash(hash).build();
        urlRepository.save(urlEntity);

        var urlRedis = urlMapper.toUrlRedis(urlEntity);

        try {
            urlRedisRepository.save(urlRedis);
        }
        catch (Exception ex) {
            log.error("Cannot save item to redis: {}", ex.getMessage(), ex);
        }

        return resultUrl;
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.UrlMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final LocalCache localCache;
    private final UrlMapper urlMapper;

    @Cacheable(value = "url", key = "#hash")
    public String getUrl(String hash) {
        Url url = urlRepository.findByHash(hash);
        if (url == null) {
            throw new ResourceNotFoundException("No such url: " + hash);
        }
        return url.getUrl();
    }

    public String saveUrlGetHash(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(localCache.getHash());
        log.info(url.toString());
        return urlRepository.save(url).getHash();
    }

}

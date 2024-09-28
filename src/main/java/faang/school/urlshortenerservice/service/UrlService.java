package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.UrlMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final LocalCache localCache;
    private final UrlMapper urlMapper;

    public String getUrl(String hash) {
        return ofNullable(urlCacheRepository.getUrl(hash))
                .orElseGet(() -> urlRepository.findById(hash)
                        .map(Url::getUrl)
                        .orElseThrow(() -> new ResourceNotFoundException(hash)));
    }

    public String saveUrlGetHash(UrlDto urlDto) {
        Url url = urlMapper.toEntity(urlDto);

        return saveUrl(url).getHash();
    }

    public Url saveUrl(Url url) {
        url.setHash(localCache.getHash());
        urlRepository.save(url);
        urlCacheRepository.saveUrl(url);

        return url;
    }

}

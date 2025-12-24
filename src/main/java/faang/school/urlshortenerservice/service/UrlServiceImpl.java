package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.entity.UrlCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public UrlDto getShortUrl(UrlDto urlDto) {
        log.info("Getting the short Url");
        Url url = urlMapper.toUrl(urlDto);
        url.setHash(hashCache.getHash());
        url = urlRepository.save(url);
        log.info("The url is saved into DB");
        UrlCache urlCache = UrlCache.builder()
                .hash(url.getHash())
                .url(url.getUrl())
                .build();
        urlCacheRepository.save(urlCache);
        log.info("The url is saved into Redis");
        return urlMapper.toUrlDto(url);
    }

    @Override
    @Transactional
    public String getOriginalUrl(String hash) {
        Optional<UrlCache> urlCache = urlCacheRepository.findById(hash);
        if (urlCache.isPresent()) {
            return urlCache.get().getUrl();
        }
        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new UrlNotFoundException(String.format("Hash %s not found", hash)));
        return url.getUrl();
    }
}

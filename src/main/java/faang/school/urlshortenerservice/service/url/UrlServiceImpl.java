package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlMapper urlMapper;

    @Override
    @Transactional
    public UrlDto createUrlHash(URL url) {

        String hash = hashCache.pop();

        Url entity = urlMapper.toEntity(url, hash);

        entity = urlRepository.saveAndFlush(entity);
        urlCacheRepository.saveUrlByHash(hash, url.toString());

        log.info("Saved new URL mapping: {}", entity);

        return urlMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public String getUrlFromHash(String hash) {

         Optional<String> cacheUrl = urlCacheRepository.getUrlByHash(hash);

         if (cacheUrl.isPresent()) {
             return cacheUrl.get();
         }

        Url entityUrl = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url with hash=" + hash + " not found"));

         return entityUrl.getUrl().toString();
    }

    @Override
    @Transactional
    public void deleteOldUrls() {

        LocalDateTime fromDate = LocalDateTime.now().minusYears(1L);
        List<String> hashes = urlRepository.removeOldAndGetHashes(fromDate);

        if (!hashes.isEmpty()) {
            hashRepository.saveAll(hashes);
        }
    }
}

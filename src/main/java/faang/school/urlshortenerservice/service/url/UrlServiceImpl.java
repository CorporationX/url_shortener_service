package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Override
    public ShortUrlDto createShortUrl(UrlDto urlDto, HttpServletRequest httpServletRequest) {
        log.debug("Received Dto {},", urlDto);
        if (urlRepository.findByUrl(urlDto.getUrl()).isPresent()) {
            String hash = urlRepository.findByUrl(urlDto.getUrl()).get().getHash();
            String shortUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + "/" + hash;
            return (new ShortUrlDto(shortUrl, hash));
        }
        log.info("Start create Short Url for Url: {}", urlDto.getUrl());
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);
        String shortUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + "/" + hash;
        urlCacheRepository.save(hash, url);
        return (new ShortUrlDto(shortUrl, hash));
    }


    @Override
    public UrlDto getUrl(String hash) {
        Url urlFromCache = urlCacheRepository.get(hash);
        if (urlFromCache == null) {
            log.debug("Url for hash: {} was not found in cache, start searching in url Repo",
                    hash);
            Url urlFromRepo = urlRepository.findById(hash)
                    .orElseThrow(() -> {
                        String errorMessage = String.format("Url for Hash: %s was not found in url repo", hash);
                        log.error(errorMessage);

                        return new EntityNotFoundException(errorMessage);
                    });
            return urlMapper.tDto(urlFromRepo);
        }
        return urlMapper.tDto(urlFromCache);
    }

    @Transactional
    @Override
    public void reuseOldUrls(int daysCount) {
        log.info("Starting old hashes delete");
        List<String> oldHashes = urlRepository.reuseOldUrls(daysCount);
        log.info("Found old hashes quantity : {} hashes", oldHashes.size());
        hashRepository.save(oldHashes);
        log.info("Old hashes delete was finished");
    }
}
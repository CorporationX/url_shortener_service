package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlEncodeDto;
import faang.school.urlshortenerservice.exception.InvalidUrlFormatException;
import faang.school.urlshortenerservice.exception.UrlNotFound;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.Utils;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private static final String URL_NOT_FOUND = "url by hash [{}] not found";
    private static final String INVALID_URL = "invalid url";

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final Utils utils;

    public String redirectByHash(String hash) {
        try {
            String url = urlCacheRepository.findByHash(hash)
                .orElseGet(() -> findHashInRepository(hash));
            urlCacheRepository.addUrl(urlMapper.toUrlDto(url, hash));
            return url;
        } catch (RedisConnectionException | RedisConnectionFailureException e) {
            return findHashInRepository(hash);
        }
    }

    @Transactional
    public String encodeUrl(UrlEncodeDto urlEncodeDto) {
        log.debug("encodeUrl urlDto: {}", urlEncodeDto);
        if (!utils.isUrlValid(urlEncodeDto.url())) {
            throw new InvalidUrlFormatException(INVALID_URL);
        }
        String hash = getExistingHash(urlEncodeDto);
        if (hash != null) {
            return hash;
        }

        String newHash = hashCache.getNewHash();
        Url url = urlMapper.toUrl(urlEncodeDto, newHash);
        urlRepository.save(url);

        try {
            UrlDto urlDto = urlMapper.toUrlDto(urlEncodeDto, newHash);
            urlDto.setHash(newHash);
            urlCacheRepository.addUrl(urlDto);
        } catch (RedisConnectionException | RedisConnectionFailureException ignored) {
            // !!! do nothing !!!
        }

        return newHash;
    }

    @Transactional
    public void clearOldUrls(int interval) {
        log.debug("interval for clear old url is: {} months", interval);
        List<Url> deletedUrl = urlRepository.clearOldUrls(interval);
        List<String> deletedHashes = deletedUrl.stream().map(Url::getHash).toList();
        List<String> deletedUrls = deletedUrl.stream().map(Url::getUrl).toList();

        try {
            urlCacheRepository.deleteHashes(deletedHashes);
            urlCacheRepository.deleteUrls(deletedUrls);
        } catch (RedisConnectionException | RedisConnectionFailureException ignored) {
            // !!! do nothing !!!
        }

        List<Hash> restoringHashes = deletedHashes.stream()
            .map(Hash::new)
            .toList();
        hashRepository.saveAll(restoringHashes);
    }

    private String getExistingHash(UrlEncodeDto urlEncodeDto) {
        try {
            Optional<String> hash = Optional.ofNullable(urlCacheRepository.findByUrl(urlEncodeDto.url())
                .orElseGet(() -> findUrlInRepository(urlEncodeDto)));
            if (hash.isPresent()) {
                urlCacheRepository.addUrl(urlMapper.toUrlDto(urlEncodeDto, hash.get()));
                return hash.get();
            }
        } catch (RedisConnectionException | RedisConnectionFailureException e) {
            log.warn(e.getMessage(), e);
            return findUrlInRepository(urlEncodeDto);
        }
        return null;
    }

    private String findHashInRepository(String hash) {
        return urlRepository.findByHash(hash)
            .map(Url::getUrl)
            .orElseThrow(() -> new UrlNotFound(utils.format(URL_NOT_FOUND, hash)));
    }

    private String findUrlInRepository(UrlEncodeDto urlEncodeDto) {
        return urlRepository.findByUrl(urlEncodeDto.url())
            .map(Url::getHash)
            .orElse(null);
    }
}

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        String url = urlCacheRepository.findByHash(hash)
            .orElseGet(() -> urlRepository.findByHash(hash)
                .map(Url::getUrl)
                .orElseThrow(() -> new UrlNotFound(utils.format(URL_NOT_FOUND, hash))));

        urlCacheRepository.addUrl(urlMapper.toUrlDto(url, hash));
        return url;
    }

    @Transactional
    public String encodeUrl(UrlEncodeDto urlEncodeDto) {
        log.debug("encodeUrl urlDto: {}", urlEncodeDto);
        if (!utils.isUrlValid(urlEncodeDto.url())) {
            throw new InvalidUrlFormatException(INVALID_URL);
        }
        Optional<String> hash = Optional.ofNullable(urlCacheRepository.findByUrl(urlEncodeDto.url())
            .orElseGet(() -> urlRepository.findByUrl(urlEncodeDto.url())
                .map(Url::getHash)
                .orElse(null)));
        if (hash.isPresent()) {
            urlCacheRepository.addUrl(urlMapper.toUrlDto(urlEncodeDto, hash.get()));
            return hash.get();
        }

        String newHash = hashCache.getNewHash();

        Url url = urlMapper.toUrl(urlEncodeDto, newHash);
        urlRepository.save(url);

        UrlDto urlDto = urlMapper.toUrlDto(urlEncodeDto, newHash);
        urlDto.setHash(newHash);
        urlCacheRepository.addUrl(urlDto);
        return newHash;
    }

    @Transactional
    public void clearOldUrls() {
        List<Url> deletedUrl = urlRepository.clearOldUrls();
        List<String> deletedHashes = deletedUrl.stream().map(Url::getHash).toList();
        List<String> deletedUrls = deletedUrl.stream().map(Url::getUrl).toList();

        urlCacheRepository.deleteHashes(deletedHashes);
        urlCacheRepository.deleteUrls(deletedUrls);

        List<Hash> restoringHashes = deletedHashes.stream()
            .map(Hash::new)
            .toList();
        hashRepository.saveAll(restoringHashes);
    }
}

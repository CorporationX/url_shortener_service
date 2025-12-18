package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.url.CreateUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImp implements UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${server.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public void cleanExpiredHashes(int yearsAgoToDeleteHashes) {
        List<String> hashes = urlRepository.getHashBatchAndDelete(yearsAgoToDeleteHashes);
        log.info("Got hashes elder than {} years from url repository and deleted them. Quantity: {}",
                yearsAgoToDeleteHashes, hashes.size());

        hashRepository.saveHashes(hashes);
        log.info("Saved deleted from url repository hashes to hash repository. Quantity: {}", hashes.size());
    }

    @Override
    public String createShortUrl(CreateUrlDto createUrlDto) {
        extractBaseUrlAndValidate(createUrlDto.url());
        String hash = hashCache.getHash();

        Url urlToSave = Url.builder()
                .hash(hash)
                .url(createUrlDto.url())
                .build();

        urlRepository.save(urlToSave);
        log.info("Save url {} with hash {} in base", urlToSave.getUrl(), urlToSave.getHash());

        urlCacheRepository.save(urlToSave.getHash(), urlToSave.getUrl());
        log.info("Save url {} with hash {} in redis", urlToSave.getUrl(), urlToSave.getHash());

        return "%s/%s".formatted(baseUrl, hash);
    }

    @Override
    public String getOriginalUrl(String hash) {
        return urlCacheRepository.get(hash)
                .orElseGet(() -> urlRepository.getByHashOrThrow(hash).getUrl());
    }

    private String extractBaseUrlAndValidate(String urlToValidate) {
        URL url;

        try {
            url = new URL(urlToValidate);
        } catch (MalformedURLException e) {
            String errorMessage = "Provided wrong url " + urlToValidate;
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        return "%s://%s/".formatted(url.getProtocol(), url.getHost());
    }
}
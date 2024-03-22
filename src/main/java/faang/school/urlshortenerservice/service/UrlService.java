package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
    private final UrlCashRepository urlCashRepository;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlMapper urlMapper;

    private static final String URL_PATTERN = "https://corpX.com/"; // in property?

    @Transactional
    public UrlDto createShortUrl(UrlDto urlDto) {
        String hashExist = getHashIfExistUrl(urlDto.getUrl());
        if (hashExist != null) {
            return new UrlDto(URL_PATTERN + hashExist);
        }

        String hash = hashCache.getHash();
        String shortUrl = URL_PATTERN + hash;
        Url url = new Url();
        url.setUrl(urlDto.getUrl());
        url.setHash(hash);
        urlRepository.save(url);
        urlCashRepository.save(urlMapper.toUrlCash(url));

        return new UrlDto(shortUrl);
    }

    private String getHashIfExistUrl(String urlInput) {
        UrlCash urlCash = urlCashRepository.findByUrl(urlInput);
        if (urlCash != null) {
            return urlCash.getHash();
        }
        Url url = urlRepository.findUrlByUrl(urlInput);
        if (url != null) {
            urlCashRepository.save(urlMapper.toUrlCash(url));
            return url.getHash();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {

        Optional<UrlCash> urlCash = urlCashRepository.findById(hash);
        if (urlCash.isPresent()) {
            return urlCash.get().getUrl();
        }
        String url = urlRepository.findById(hash)
                .orElseThrow(() -> new EntityNotFoundException("Error or outdated parameter"))
                .getUrl();
        urlCashRepository.save(new UrlCash(hash, url));
        return url;
    }

    @Transactional
    public void cleanHash() {
        log.info("Started clearing URL records");
        List<String> freeHashes = urlRepository.deleteOldUrl();
        freeHashes.forEach(hash -> hashRepository.save(new Hash(hash)));
        log.info("Count cleared old URL records - {}", freeHashes.size());
    }
}
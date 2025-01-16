package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.FullUrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlMapper urlMapper;
    private final HashCache hashCache;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${hash.year-to-life}")
    private int yearToLife;

    @Transactional
    public String createShortUrl(UrlDto urlDto) {
        urlDto.setHash(hashCache.getHash());
        Url url = urlMapper.toEntity(urlDto);
        urlRepository.save(url);
        urlCacheRepository.save(url);
        log.info("Save new url {}", urlDto.getUrl());
        return urlDto.getHash();
    }

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String fullUrl = urlCacheRepository.findByHash(hash);
        if (fullUrl != null) {
            return fullUrl;
        }
        Url url = urlRepository.findById(hash).orElseThrow(() -> new FullUrlNotFoundException(hash));
        return url.getUrl();
    }

    @Transactional
    public void removeOldUrls() {
        List<String> hashes = urlRepository.deleteAndGetOldUrls(LocalDate.now().minusYears(yearToLife));
        List<Hash> hashesEntity = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashesEntity);
        urlCacheRepository.deleteHashes(hashes);
    }
}
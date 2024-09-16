package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
    private final UrlValidator urlValidator;
    private final UrlMapper urlMapper;
    private final HashMapper hashMapper;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final URLCacheRepository urlCacheRepository;

    @Value("${url.host}")
    private String host;

    @Value("${url.cleaner.period}")
    private String periodConfig;

    @Transactional
    public HashDto createShortLink(UrlDto urlDto) {
        if(urlValidator.validateUrlByAlreadyExists(urlDto.getUrl())) {
            Url url = urlRepository.findByUrl(urlDto.getUrl());
            return convertToHashDto(url.getHash());
        }

        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hashCache.getHash());
        url = urlRepository.save(url);
        urlCacheRepository.save(url.getHash(), url.getUrl());
        return convertToHashDto(url.getHash());
    }

    @Transactional
    public void removeOldUrl() {
        Period period = Period.parse(periodConfig);
        LocalDateTime thresholdDate = LocalDateTime.now().minus(period);

        List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes(thresholdDate);

        List<Hash> hashes = freedHashes.stream()
                .map(Hash::new)
                .collect(Collectors.toList());

        hashRepository.saveAll(hashes);
    }

    @Transactional(readOnly = true)
    public String getUrlByHash(String hash) {

        return urlCacheRepository.getUrl(hash).orElseGet(() -> {
            Url dbUrl = urlRepository.findById(hash)
                    .orElseThrow(() -> new UrlNotFoundException("URL not found for hash: " + hash));

            urlCacheRepository.save(dbUrl.getHash(), dbUrl.getUrl());

            return dbUrl.getUrl();
        });
    }



    private HashDto convertToHashDto(String hashValue) {
        Hash hash = new Hash(host + hashValue);
        return hashMapper.toDto(hash);
    }
}

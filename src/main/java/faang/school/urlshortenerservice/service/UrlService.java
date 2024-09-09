package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final URLCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;
    private final HashCache hashCache;
    private final HashMapper hashMapper;

    @Value("${url.host.}")
    private String host;

    @Transactional
    public HashDto createShortLink(URLDto urlDto) {
        String hash = hashCache.getHash();

        URL url = URL.builder()
                .url(urlDto.getUrl())
                .hash(hash)
                .build();

        urlRepository.save(url);
        urlCacheRepository.save(url);

        return hashMapper.toDto(new Hash(host + hash));
    }

    @Transactional
    public void deleteOldURL() {
        List<String> hashes = urlRepository.getHashAndDeleteURL();
        if (hashes.isEmpty()) {
            log.info("No old URL in database");
            return;
        }
        hashRepository.saveAll(hashes.stream()
                .map(Hash::new)
                .toList());
        log.info("Deleted old URLs and saved {} hashes.", hashes.size());
    }
}
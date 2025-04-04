package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCleanerService {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public void cleanupOutdatedHashes() {
        List<String> retrievedHashes = urlRepository.deleteExpiredUrlsAndReturnHashes();
        if (retrievedHashes.isEmpty()) {
            log.info("no outdated short links found");
            return;
        }

        log.info("{} of outdated short links found and removed.", retrievedHashes.size());
        hashRepository.saveAll(retrievedHashes);
        retrievedHashes.forEach(urlCacheRepository::deleteByHash);
    }}

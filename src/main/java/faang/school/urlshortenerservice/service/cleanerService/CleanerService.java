package faang.school.urlshortenerservice.service.cleanerService;

import faang.school.urlshortenerservice.config.hash.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.hashGenerator.HashGenerator;
import faang.school.urlshortenerservice.service.urlService.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanerService {

    private final UrlService urlService;
    private final HashProperties hashProperties;
    private final HashRepository hashRepository;


    @Transactional
    @Async("urlThreadPool")
    public void clearExpiredUrls() {
        List<Url> releasedUrls = urlService.findAndReturnExpiredUrls(hashProperties.getExpirationUrl());
        List<Hash> releasedHashes = releasedUrls.stream()
                .map(Url::getHash)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(releasedHashes);
        log.info("clearExpiredUrls - finish, released hashes size - {}", releasedHashes.size());
    }
}

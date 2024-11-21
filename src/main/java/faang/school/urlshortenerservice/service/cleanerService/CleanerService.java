package faang.school.urlshortenerservice.service.cleanerService;

import faang.school.urlshortenerservice.config.сache.CacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.urlService.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanerService {

    private final UrlService urlService;
    private final CacheProperties cacheProperties;
    private final HashRepository hashRepository;

    @Transactional
    public void clearExpiredUrls() {
        List<Url> releasedUrls = urlService.findAndReturnExpiredUrls(cacheProperties.getUrlCleaningInterval());
        List<Hash> releasedHashes = releasedUrls.stream()
                .map(Url::getHash)
                .map(Hash::new)
                .toList();
        hashRepository.saveAllBatched(releasedHashes);
        log.info("clearExpiredUrls - finish, released hashes size - {}", releasedHashes.size());
    }
}

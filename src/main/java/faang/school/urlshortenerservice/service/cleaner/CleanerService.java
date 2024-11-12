package faang.school.urlshortenerservice.service.cleaner;

import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.url.UrlService;
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
    private final HashService hashService;

    @Async("urlThreadPool")
    @Transactional
    public void clearExpiredUrls() {
        List<Url> releasedUrls = urlService.findAndReturnExpiredUrls();
        List<String> releasedHashes = releasedUrls.stream()
                .map(Url::getHash)
                .toList();
        hashService.saveRangeHashes(releasedHashes);
        log.debug("clearExpiredUrls - finish, released hashes - {}", releasedHashes);
    }
}

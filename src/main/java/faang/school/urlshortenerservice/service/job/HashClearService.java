package faang.school.urlshortenerservice.service.job;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashClearService {
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Transactional
    public long deleteOldUrl() {
        List<String> deletedUrls = urlRepository.deleteOldUrls();
        deletedUrls.forEach(urlCacheRepository::removeUrl);
        return hashService.saveFreeHashes(deletedUrls);
    }
}

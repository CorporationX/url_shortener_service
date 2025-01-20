package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanHashesService {
    private final UrlService urlService;
    private final HashService hashService;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public void cleanHashes() {
        List<String> cleanedHashes = urlService.cleanHashes();
        hashService.saveAll(cleanedHashes);
        urlCacheRepository.deleteAll(cleanedHashes);
    }
}

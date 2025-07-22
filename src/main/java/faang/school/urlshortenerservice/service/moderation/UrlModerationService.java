package faang.school.urlshortenerservice.service.moderation;

import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.HashServiceImpl;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlModerationService {
    private final UrlServiceImpl urlService;
    private final HashService hashService;
    @Transactional
    public void deleteUrlOlderOneYearAndSaveByHash(int limit) {
        urlService.deleteUrlOlderOneYearAndSaveByHash(limit);
    }
    @Transactional(readOnly = true)
    public int countOldUrls(){
        return urlService.countUrlsOlder();
    }
}

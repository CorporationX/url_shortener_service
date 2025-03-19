package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {

    private final HashService hashService;
    private final UrlService urlService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanUrlsAndSaveHashes(List<Url> urls) {
        List<String> hashesToSave = urls.stream()
                .map(Url::getHash)
                .toList();

        hashService.saveHashes(hashesToSave);
        urlService.deleteUrls(urls);
    }
}

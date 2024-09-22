package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.manage.HashManager;
import faang.school.urlshortenerservice.manage.UrlManager;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.validation.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlManager urlManager;
    private final HashManager hashManager;
    private final UrlValidator urlValidator;

    public String createShortUrl(String getUrl) {
        urlValidator.validateUrl(getUrl);
        String hash = hashManager.getHash();
        Url url = urlManager.saveUrl(hash, getUrl);
        urlManager.addCache(url);
        return url.getHash();
    }

    public String getUrl(String hash) {
        return urlManager.getUrl(hash);
    }

    @Transactional
    public void clearExpiredHashes(LocalDateTime expirationDate) {
        List<String> expiredHashes = urlManager.getExpiredHashesAndDelete(expirationDate);
        hashManager.saveHashes(expiredHashes);
    }
}

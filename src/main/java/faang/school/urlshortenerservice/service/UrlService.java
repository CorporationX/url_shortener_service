package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.ValidationException;
import faang.school.urlshortenerservice.manager.HashManager;
import faang.school.urlshortenerservice.manager.UrlRepositoryManager;
import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashManager hashManager;
    private final UrlRepositoryManager urlRepositoryManager;
    @Value("${service.base-prefix}")
    private String basePrefix;

    @Transactional
    public String generateShortUrl(String url) {
        String hash = hashManager.getHash();
        Url savingUrl = Url.builder()
                .url(url)
                .hash(hash)
                .build();
        urlRepositoryManager.save(savingUrl);
        return basePrefix.concat(hash);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String hash) {
        String url = urlRepositoryManager.getOriginalUrl(hash);
        if (url == null) {
            throw new ValidationException("Not found Url with hash: " + hash);
        }
        return url;
    }
}

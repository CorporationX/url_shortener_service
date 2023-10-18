package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private  final UrlCacheRepository urlCacheRepository;

    @Transactional(readOnly = true)
    public String getUrl(String hash) {
        String url = urlCacheRepository.getUrl(hash);

        if (url == null) {
            Url urlRep = urlRepository.findByHash(hash).orElseThrow(() -> new EntityNotFoundException("Url not found"));
            url = urlRep.getUrl();
        }
        return url;
    }
}

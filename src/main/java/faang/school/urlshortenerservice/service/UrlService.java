package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.UrlRepository;
import faang.school.urlshortenerservice.local_cache.LocalCache;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlServiceValidator urlServiceValidator;
    private final LocalCache localCache;

    // Не доделано
    @Transactional
    public URL createNewShortUrl(String url){
        urlServiceValidator.checkUrl(url);

        String hash = localCache.getCache();

        Url entityUrl = new Url();
        entityUrl.setHash(hash);
        entityUrl.setUrl(url);

        urlRepository.save(entityUrl);


    }
}

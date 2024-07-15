package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.search.SearchesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final List<SearchesService> urlServices;

    @Transactional
    public String transformUrlToHash(String url) {
        String hashValue = hashCache.getRandomHashFromCache();
        urlRepository.save(Url.builder().url(url).hash(hashValue).createdAt(LocalDateTime.now()).build());
        hashCache.saveToCache(hashValue, url);
        return hashValue;
    }

    @Transactional
    public String getUrlFromHash(String hash) {
        return urlServices.stream().map(service -> service.findUrl(hash)).filter(Optional::isPresent)
                .map(Optional::get).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found for hash: " + hash));
    }
}

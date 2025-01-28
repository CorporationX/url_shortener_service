package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cach.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public String saveNewHash(UrlDto urlDto) {
        Hash newHash = hashCache.getHash();
        Url newUrl = Url.builder().hash(newHash.getHash()).url(urlDto.url()).build();
        saveToDb(newUrl);
        urlCacheRepository.addToRedis(newUrl);
        return newUrl.getHash();
    }

    public void saveToDb(Url url) {
        urlRepository.save(url);
    }

    public String searchUrl(String hash) {
        String url = urlCacheRepository.searchInRedis(hash);
        if (url == null) {
            Url urlFromDb = urlRepository.findById(hash).orElseThrow(() -> new EntityNotFoundException("Урл не найден"));
            url = urlFromDb.getUrl();
        }
        return url;
    }
}

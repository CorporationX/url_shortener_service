package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.BusinessException;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final HashService hashService;
    private final UrlRepository urlRepository;

    @Value("${server.host}")
    private String serverHost;

    @Value("${server.port}")
    private String serverPort;

    public UrlReadDto shortenUrl(String url) {
        String hash = hashCache.getCachedHash();
        urlRepository.save(new Url(hash, url));
        return new UrlReadDto(getShortenedUrl(hash));
    }

    @Cacheable(value = "url", key = "#hash")
    public String getOriginalUrl(String hash) {
        return urlRepository.findByHash(hash).map(Url::getUrl)
                .orElseThrow(() -> new EntityNotFoundException("Url для такого хэша не найден"));
    }

    @Transactional
    public void deleteOldUrls(LocalDateTime afterDeleteDate) {
        if (afterDeleteDate.isAfter(LocalDateTime.now())) {
            throw new BusinessException("Удалить можно только старые ссылки");
        }
        List<Url> deletedUrls = urlRepository.deleteOldUrls(afterDeleteDate);
        hashService.saveHashes(deletedUrls.stream().map(Url::getHash).toList());
    }

    private String getShortenedUrl(String hash) {
        return "https://" + serverHost + ":" + serverPort + "/" + hash;
    }
}

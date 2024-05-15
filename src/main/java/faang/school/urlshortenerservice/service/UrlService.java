package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UrlService {

    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public String convertToShortUrl(UrlDto urlDto) {
        return null;
    }

    public String redirectOriginalUrl(String hash) {
        return urlCacheRepository.getByHash(hash).orElseGet(
                () -> urlRepository.getByHash(hash).map(Url::getUrl).orElseThrow(
                        () -> new EntityNotFoundException("URL not found for hash: " + hash)));
    }
}

package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

  private final UrlShortenerProperties properties;
  private final HashCacheImpl hashCache;
  private final UrlRepository urlRepository;
  private final UrlCacheRepository urlCacheRepository;

  @Transactional
  public String makeShortUrl(UrlDto urlDto) {
    String hash = Optional.ofNullable(hashCache.getHash())
        .orElseThrow(() -> new IllegalStateException("No hash available"));
    urlRepository.saveUrl(new Url(hash, urlDto.getLongUrl(), LocalDateTime.now()));
    urlCacheRepository.saveUrl(hash, urlDto.getLongUrl());

    return String.format("%s/%s", properties.getBaseUrl(), hash);
  }

  public String getLongUrl(String hash) {
    return urlCacheRepository.getUrl(hash)
        .orElseGet(() -> urlRepository.findUrlByHash(hash)
            .map(url -> {
              urlCacheRepository.saveUrl(hash, url);
              return url;
            })
            .orElseThrow(() -> new UrlNotFoundException(
                String.format("url with hash %s not found", hash))));
  }
}

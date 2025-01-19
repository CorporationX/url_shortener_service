package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlServiceImpl implements UrlService {

  private final HashGenerator hashGenerator;
  private final HashCache hashCache;
  private final UrlRepository urlRepository;
  private final UrlMapper urlMapper;
  private final UrlCacheRepository urlCacheRepository;

  @Transactional
  @Override
  public UrlResponseDto createShortUrl(UrlCreateDto dto) {
    String hash = hashCache.getHash();
    Url url = urlRepository.create(dto.url(), hash);
    urlCacheRepository.add(url);

    return urlMapper.toDto(url);

  }

  @Override
  public String getOriginalUrl(String hash) {

    String url = urlCacheRepository.findUrlByHash(hash);

    if (url == null) {
      url = urlRepository.findById(hash).orElseThrow(() -> new NoSuchElementException("No such URL found")).getUrl();
    }

    return url;
  }

}

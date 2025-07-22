package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@AllArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;

    @Value("${url.short_prefix}")
    private final String shortUrlPrefix;

    @Transactional
    @Override
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        log.info("Start create Short Url");
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);

        String shortUrl = shortUrlPrefix + hash;
        return (ShortUrlDto.builder()
                .shortUrl(shortUrl).build());
    }



    @Override
    public UrlDto getUrl(UrlDto urlDto) {
        return null;
    }
}

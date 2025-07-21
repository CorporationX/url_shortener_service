package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final H

    @Override
    public ShortUrlDto createShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);



    }

    @Override
    public UrlDto getUrl(UrlDto urlDto) {
        return null;
    }
}

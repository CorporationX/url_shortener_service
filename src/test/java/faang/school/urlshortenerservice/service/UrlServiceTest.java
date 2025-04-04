package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;
    @Spy
    private UrlMapperImpl urlMapper;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    public void createShortUrl() {
        String originalUrl = "http://example.com";
        String hash = "TEST";
        UrlCreateDto createDto = new UrlCreateDto();
        createDto.setOriginalUrl(originalUrl);

        Url url = new Url();
        url.setUrl(originalUrl);
        url.setHash(hash);

        UrlReadDto urlReadDto = new UrlReadDto();
        urlReadDto.setOriginalUrl(originalUrl);
        urlReadDto.setHash(hash);

        Mockito.when(hashCache.getHash()).thenReturn(hash);
        Mockito.when(urlCacheRepository.save(Mockito.any(Url.class))).thenReturn(url);

        UrlReadDto result = urlService.createShortUrl(createDto);

        assertEquals(originalUrl, result.getOriginalUrl());
        assertEquals(hash, result.getHash());
        Mockito.verify(hashCache).getHash();
        Mockito.verify(urlCacheRepository).save(Mockito.any(Url.class));
    }
}
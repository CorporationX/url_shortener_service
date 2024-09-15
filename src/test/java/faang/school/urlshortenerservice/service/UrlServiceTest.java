package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;
    @Spy
    private UrlMapper urlMapper;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    private Url url;
    private UrlDto urlDto;
    private String hash;

    @BeforeEach
    void setUp() {
        String urlStr = "url";
        hash = "hash";
        url = Url.builder()
                .url(urlStr)
                .build();
        urlDto = UrlDto.builder()
                .url(urlStr)
                .build();
    }

    @Test
    void createShortUrl() {
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(url)).thenReturn(url);

        urlService.createShortUrl(urlDto);

        verify(urlMapper, times(1)).toEntity(urlDto);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(url);
        verify(urlCacheRepository, times(1)).save(hash, url.getUrl());
        verify(urlMapper, times(1)).toDto(url);
    }
}
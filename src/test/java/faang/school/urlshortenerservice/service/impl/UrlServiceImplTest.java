package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.cache.HashCache;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    // TODO asdfasd

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private final String shortUrlDomain = "http://denart.info/";
    private final String hash = "987";

    @Test
    public void testCreateShortUrlSuccess() {
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("https://google.com");
        when(hashCache.getHash()).thenReturn(hash);
        ReflectionTestUtils.setField(urlService, "shortUrlDomain", shortUrlDomain);

        String result = urlService.createShortUrl(urlDto);

        verify(urlRepository, times(1)).save(Mockito.any());
        assertEquals(shortUrlDomain + hash, result);
    }

    @Test
    public void testGetOriginalUrlSuccess() {
        Url url = Url.builder()
                .url("https://google.com").build();
        ReflectionTestUtils.setField(urlService, "shortUrlDomain", shortUrlDomain);
        when(urlCacheRepository.get(hash)).thenReturn(url.getUrl());

        String result = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).get(hash);
        assertEquals(url.getUrl(), result);
    }
}
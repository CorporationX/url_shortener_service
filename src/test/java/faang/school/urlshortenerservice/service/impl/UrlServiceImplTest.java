package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.cache.HashCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    // TODO asdfasd

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private String shortUrlDomain = "http://denart.info/";
    private String hash = "987";

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
    public void testGetOriginalUrlSuccess() throws InterruptedException {
        String shortUrl = "http://denart.info/" + hash;
        Url url = Url.builder()
                .url("https://google.com").build();
        ReflectionTestUtils.setField(urlService, "shortUrlDomain", shortUrlDomain);
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(shortUrl);

        verify(urlRepository, times(1)).findUrlByHash(Mockito.anyString());
        assertEquals(url.getUrl(), result);
    }
}
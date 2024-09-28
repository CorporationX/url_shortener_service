package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Spy
    private UrlMapper urlMapper;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    private UrlDto urlDto;
    private String expectedShortUrl;

    @BeforeEach
    void setUp() {
        urlDto = new UrlDto("hash", "url");
        expectedShortUrl = "null/hash";
    }

    @Test
    public void testGetShortUrlFromUrlCacheRepo() {
        when(urlCacheRepository.getHashByUrl(urlDto.getUrl())).thenReturn(urlDto.getHash());

        String result = urlService.getShortUrl(urlDto);

        assertEquals(expectedShortUrl, result);
    }

    @Test
    public void testGetShortUrlFromUrlRepo() {
        when(urlCacheRepository.getHashByUrl(urlDto.getUrl())).thenReturn(null);
        when(urlRepository.findHashByUrl(urlDto.getUrl())).thenReturn(urlDto.getHash());
        doNothing().when(urlCacheRepository).save(urlDto);

        String result = urlService.getShortUrl(urlDto);

        assertEquals(expectedShortUrl, result);
    }

    @Test
    public void testGetShorUrlFromHashCache() {
        when(urlCacheRepository.getHashByUrl(urlDto.getUrl())).thenReturn(null);
        when(urlRepository.findHashByUrl(urlDto.getUrl())).thenReturn(null);
        when(hashCache.getHash()).thenReturn(urlDto.getHash());
        when(urlRepository.save(any())).thenReturn(new Url());
        doNothing().when(urlCacheRepository).save(urlDto);

        String result = urlService.getShortUrl(urlDto);

        assertEquals(expectedShortUrl, result);
    }

    @Test
    public void testGetOriginalLinkFromUrlCacheRepo() {
        String expected = urlDto.getUrl();

        when(urlCacheRepository.getUrlByHash(urlDto.getHash())).thenReturn(expected);

        String result = urlService.getOriginalLink(urlDto.getHash());

        assertEquals(expected, result);
    }

    @Test
    public void testGetOriginalLinkShouldThrow() {
        when(urlCacheRepository.getUrlByHash(urlDto.getHash())).thenReturn(null);
        when(urlRepository.findById(urlDto.getHash())).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalLink(urlDto.getHash()));
    }

    @Test
    public void testGetOriginalLinkFromUrlRepo() {
        String expected = urlDto.getUrl();
        Url url = new Url("hash", "url", LocalDateTime.now());

        when(urlCacheRepository.getUrlByHash(urlDto.getHash())).thenReturn(null);
        when(urlRepository.findById(urlDto.getHash())).thenReturn(Optional.of(url));
        doNothing().when(urlCacheRepository).save(any());

        String result = urlService.getOriginalLink(urlDto.getHash());

        assertEquals(expected, result);
    }
}
package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlService urlService;

    private String url = "http://host:port/";
    private String longUrl = "http://someUrl.com";
    private String hash = "hash";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "url", url);
    }

    @Test
    public void testCreateShortUrl() {
        Url newUrl = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();
        when(hashCache.getHash()).thenReturn(hash);
        UrlResponseDto expected = new UrlResponseDto(url + hash);

        UrlResponseDto result = urlService.createShortUrl(longUrl);

        verify(urlRepository).save(newUrl);
        verify(urlCacheRepository).save(hash, longUrl);
        assertEquals(expected, result);
    }

    @Test
    public void testGetShortUrlByHashFromCache() {
        when(urlCacheRepository.findLongUrlByHash(hash)).thenReturn(longUrl);

        String result = urlService.getLongUrlByHash(hash);

        assertEquals(longUrl, result);
        verify(urlRepository, times(0)).findUrlByHash(hash);
    }

    @Test
    public void testGetShortUrlByHashFromRepo() {
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrlByHash(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository).save(hash, longUrl);
    }

    @Test
    public void testGetShortUrlByHashUrlNotFound() {
        UrlNotFoundException e = assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrlByHash(hash));
        assertEquals("Url not found for hash: " + hash, e.getMessage());
    }
}

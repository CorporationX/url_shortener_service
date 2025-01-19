package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cash.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCasheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCasheRepository urlCasheRepository;

    @Mock
    private HashCache hashCashe;

    @Mock
    private UrlMapper urlMapper;

    private String hash;
    private String url;

    private String urlPath;
    private UrlDto urlDto;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        hash = "abc123";
        url = "https://example.com";
        urlPath = "http://short.url/";


        urlDto = UrlDto.builder()
                .url(url)
                .build();
    }

    @Test
    public void testGetOriginalUrlFromCache() {
        when(urlCasheRepository.getUrl(hash)).thenReturn(url);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(url, result);
        verify(urlCasheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(0)).getUrlByHash(hash);
    }

    @Test
    public void testGetOriginalUrlFromDatabase() {
        when(urlCasheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.getUrlByHash(hash)).thenReturn(url);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(url, result);
        verify(urlCasheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).getUrlByHash(hash);
    }

    @Test
    public void testGetOriginalUrlNotFound() {
        String hash = "abc123";
        when(urlCasheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.getUrlByHash(hash)).thenThrow(new EmptyResultDataAccessException(1));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
            urlService.getOriginalUrl(hash);
        });
        assertEquals("Url with hash abc123 was not found in database", exception.getMessage());
        verify(urlCasheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).getUrlByHash(hash);
    }


    @Test
    public void testGetShotUrl() {
        ReflectionTestUtils.setField(urlService, "urlPath", urlPath);

        when(hashCashe.getHash()).thenReturn(hash);

        String result = urlService.getShotUrl(urlDto);

        assertTrue(result.endsWith(hash));
        verify(hashCashe, times(1)).getHash();
        verify(urlRepository, times(1)).saveUrlWithNewHash(hash, url);
        verify(urlCasheRepository, times(1)).saveUrl(hash, url);
    }
}
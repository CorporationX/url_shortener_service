package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.repository.HashCache;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCashRepository urlCashRepository;

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlValidator urlValidator;

    @InjectMocks
    private UrlService urlService;

    private String validUrl;
    private String invalidUrl;
    private String hash;

    @BeforeEach
    public void setUp() {
        validUrl = "http://validurl.com";
        invalidUrl = "invalidurl";
        hash = "123456";
    }

    @Test
    public void testCreateShortUrlWhenValidUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(validUrl);

        verify(urlRepository, times(1)).save(new UrlEntity(validUrl, hash));
        verify(urlCashRepository, times(1)).save(hash, validUrl);
        assertEquals(hash, result);
    }

    @Test
    public void testGetUrlByHashWhenUrlInCache() {
        when(urlCashRepository.getUrl(hash)).thenReturn(validUrl);

        String result = urlService.getUrlByHash(hash);

        verify(urlCashRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(0)).findByHash(hash);
        assertEquals(validUrl, result);
    }

    @Test
    public void testGetUrlByHashWhenUrlNotInCacheButInRepository() {
        when(urlCashRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(new UrlEntity(validUrl, hash));

        String result = urlService.getUrlByHash(hash);

        verify(urlCashRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlCashRepository, times(1)).save(hash, validUrl);
        assertEquals(validUrl, result);
    }
}
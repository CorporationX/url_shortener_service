package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashCash;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.exception.DataValidationException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
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
    private HashCash hashCash;
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
        when(hashCash.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(validUrl);

        verify(urlValidator, times(1)).validateUrl(validUrl);
        verify(urlRepository, times(1)).save(new UrlEntity(validUrl, hash));
        verify(urlCashRepository, times(1)).save(hash, validUrl);
        assertEquals(hash, result);
    }

    @Test
    public void testCreateShortUrlWhenInvalidUrl() {
        doThrow(DataValidationException.class).when(urlValidator).validateUrl(invalidUrl);

        assertThrows(DataValidationException.class, () -> urlService.createShortUrl(invalidUrl));

        verify(urlValidator, times(1)).validateUrl(invalidUrl);
        verify(urlRepository, times(0)).save(any(UrlEntity.class));
        verify(urlCashRepository, times(0)).save(anyString(), anyString());
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
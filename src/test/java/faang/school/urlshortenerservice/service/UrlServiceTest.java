package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.manage.HashManager;
import faang.school.urlshortenerservice.manage.UrlManager;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.validation.UrlValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlManager urlManager;

    @Mock
    private HashManager hashManager;

    @Mock
    private UrlValidator urlValidator;


    @Test
    @DisplayName("Should create a short URL successfully")
    public void testCreateShortUrl_Success() {
        String originalUrl = "https://www.example.com";
        String generatedHash = "abc123";

        when(hashManager.getHash()).thenReturn(generatedHash);
        when(urlManager.saveUrl(generatedHash, originalUrl)).thenReturn(new Url(generatedHash, originalUrl));

        String shortUrl = urlService.createShortUrl(originalUrl);

        assertEquals(generatedHash, shortUrl);
        verify(urlManager).addCache(any(Url.class));
    }

    @Test
    @DisplayName("Should throw exception when URL is invalid")
    public void testCreateShortUrl_InvalidUrl() {
        String invalidUrl = "invalid-url";

        doThrow(new DataValidationException("Please provide a valid URL"))
                .when(urlValidator).validateUrl(invalidUrl);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            urlService.createShortUrl(invalidUrl);
        });

        assertEquals("Please provide a valid URL", exception.getMessage());
    }

    @Test
    @DisplayName("Should retrieve the original URL for a given hash")
    public void testGetUrl_Success() {
        String hash = "abc123";
        String originalUrl = "https://www.example.com";

        when(urlManager.getUrl(hash)).thenReturn(originalUrl);

        String retrievedUrl = urlService.getUrl(hash);

        assertEquals(originalUrl, retrievedUrl);
    }

    @Test
    @DisplayName("Should clear expired hashes successfully")
    public void testClearExpiredHashes_Success() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(1);
        when(urlManager.getExpiredHashesAndDelete(expirationDate)).thenReturn(Collections.singletonList("abc123"));

        urlService.clearExpiredHashes(expirationDate);

        verify(hashManager).saveHashes(anyList());
    }
}
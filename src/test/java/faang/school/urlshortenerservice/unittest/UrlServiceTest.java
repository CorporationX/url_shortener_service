package faang.school.urlshortenerservice.unittest;

import faang.school.urlshortenerservice.model.UrlEntity;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createShortUrl_ShouldReturnShortUrl() {

        String longUrl = "https://example.com";
        String expectedHash = "1234abcd";

        when(hashGenerator.generateHash()).thenReturn(expectedHash);

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setHashUrl(expectedHash);
        urlEntity.setLongUrl(longUrl);
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

        String shortUrl = urlService.createShortUrl(longUrl);

        assertNotNull(shortUrl);
        assertTrue(shortUrl.contains(expectedHash));

        verify(urlRepository, times(1)).save(any(UrlEntity.class));
        verify(urlCacheRepository, times(1)).save(eq(expectedHash), eq(longUrl));
    }
}
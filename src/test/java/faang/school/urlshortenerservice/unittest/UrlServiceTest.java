package faang.school.urlshortenerservice.unittest;

import faang.school.urlshortenerservice.exeption.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateShortUrl() {
        // Mock Hash and Url Creation
        String longUrl = "https://example.com";
        String hash = "abc123";

        when(hashGenerator.generateHash()).thenReturn(hash);

        Hash hashEntity = Hash.builder().hash(hash).build();
        Url url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .hashEntity(hashEntity)
                .build();

        when(hashRepository.save(any(Hash.class))).thenReturn(hashEntity);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        // Test
        String shortUrl = urlService.createShortUrl(longUrl);

        assertNotNull(shortUrl);
        assertEquals("http://short.url/abc123", shortUrl);

        // Verify
        verify(hashGenerator, times(1)).generateHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(hashRepository, times(1)).save(any(Hash.class));
        verify(urlCacheRepository, times(1)).save(hash, longUrl);
    }

    @Test
    void testGetLongUrlSuccess() {
        // Mock Repository Response
        String hash = "abc123";
        String longUrl = "https://example.com";

        Url url = Url.builder().hash(hash).url(longUrl).build();
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        // Test
        String result = urlService.getLongUrl(hash);
        assertEquals(longUrl, result);

        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void testGetLongUrlNotFound() {
        // Mock Empty Response
        String hash = "abc123";
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        // Test and Assert Exception
        assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrl(hash));
    }
}
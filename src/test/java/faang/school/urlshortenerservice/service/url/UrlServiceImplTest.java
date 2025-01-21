package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import faang.school.urlshortenerservice.validator.url.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlServiceImpl urlServiceImpl;

    private UrlDto urlDto;

    @BeforeEach
    void setUp() {
        urlDto = new UrlDto("https://example.com");
    }

    @Test
    void shortenUrl_existingUrl_returnsExistingHash() {
        Url url = Url.builder()
                .hash("existingHash")
                .url("https://example.com")
                .build();
        when(urlValidator.findByUrl(urlDto.getUrl()))
                .thenReturn(url);

        String result = urlServiceImpl.shortenUrl(urlDto);

        assertEquals("existingHash", result);
    }

    @Test
    void shortenUrl_newUrl_returnsNewHash() {
        when(urlValidator.findByUrl(urlDto.getUrl())).thenReturn(null);
        when(hashCache.getHash()).thenReturn("newHash");

        String result = urlServiceImpl.shortenUrl(urlDto);

        assertEquals("newHash", result);
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save("newHash", "https://example.com");
    }

    @Test
    void shortenUrl_noHashGenerated_throwsException() {
        when(urlValidator.findByUrl(urlDto.getUrl())).thenReturn(null);
        when(hashCache.getHash()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            urlServiceImpl.shortenUrl(urlDto);
        });

        assertEquals("Failed to generate short URL", exception.getMessage());
    }
}
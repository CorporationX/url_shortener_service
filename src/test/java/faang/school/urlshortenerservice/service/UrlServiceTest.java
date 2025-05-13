package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exceptions.CacheOperationException;
import faang.school.urlshortenerservice.exceptions.HashGenerationException;
import faang.school.urlshortenerservice.exceptions.InvalidUrlException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    @InjectMocks
    private UrlService urlService;

    private final String hash = "test";
    private UrlDto urlDto;

    @BeforeEach
    public void setUp() {
        urlDto = new UrlDto("https://faang-school");
    }

    @Test
    public void testPositiveShortenUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        String urlResult = urlService.shortenUrl(urlDto);

        verify(urlRepository).insertUrl(hash, urlDto.url());
        verify(urlCacheRepository).save(hash, urlDto.url());

        assertEquals(hash, urlResult);
    }

    @Test
    public void testNegativeShortenUrlDtoIsNull() {
        assertThrows(InvalidUrlException.class, () -> urlService.shortenUrl(null));
    }

    @Test
    public void testNegativeShortenUrlDtoUrlIsNull() {
        assertThrows(InvalidUrlException.class, () -> urlService.shortenUrl(new UrlDto(null)));
    }

    @Test
    public void testNegativeShortenUrlDtoUrlIsEmpty() {
        assertThrows(InvalidUrlException.class, () -> urlService.shortenUrl(new UrlDto("")));
    }

    @Test
    public void testNegativeShortenUrlHashIsNull() {

        when(hashCache.getHash()).thenReturn(null);
        assertThrows(HashGenerationException.class, () -> urlService.shortenUrl(urlDto));
    }

    @Test
    public void testNegativeShortenUrlNoSave() {
        doThrow(new RuntimeException("DB error"))
                .when(urlRepository).insertUrl(hash, urlDto.url());
        when(hashCache.getHash()).thenReturn("test");
        assertThrows(CacheOperationException.class, () -> urlService.shortenUrl(urlDto));
    }
}

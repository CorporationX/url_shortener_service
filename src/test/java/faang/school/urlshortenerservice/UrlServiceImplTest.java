package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hash.HashPreGenerator;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashPreGenerator hashPreGenerator;

    @Spy
    private UrlMapper urlMapper = new UrlMapperImpl();

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    public void testGetOriginalUrlShouldReturnCachedUrlWhenExistsInCache() {
        String hash = "abc123";
        String cachedUrl = "https://example.com";
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(cachedUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository).getUrlByHash(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    public void testGetOriginalUrlShouldReturnFromDbAndCacheWhenNotInCache() {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        Url url = new Url(hash, originalUrl, Instant.now());

        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).save(originalUrl, hash);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    public void testGetOriginalUrlShouldThrowExceptionWhenUrlNotFound() {
        String hash = "nonExistent";
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }

    @Test
    public void testCreateAndSaveShortUrlShouldCreateShortUrlCorrectly() {
        ReflectionTestUtils.setField(urlService, "host", "https://example.com");
        String hash = "xyz789";
        String originalUrl = "https://example.com";
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(originalUrl);

        Url url = new Url();
        url.setUrl(originalUrl);
        url.setHash(hash);

        when(hashPreGenerator.getHash()).thenReturn(hash);

        String result = urlService.createAndSaveShortUrl(urlDto);

        assertEquals("https://example.com/xyz789", result);
        verify(hashPreGenerator).getHash();
        verify(urlMapper).toEntity(urlDto);
        verify(urlRepository).save(url);
        verify(urlCacheRepository).save(originalUrl, hash);
    }
}

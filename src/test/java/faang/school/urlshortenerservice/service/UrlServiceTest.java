package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.OriginalUrlRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    public static final String TEST_URL = "url";
    public static final String TEST_HASH = "hash";

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    @InjectMocks
    private UrlService urlService;

    @Test
    public void testGetUrlByHash_FoundInCache() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(new Url(TEST_HASH, TEST_URL));

        String result = urlService.getUrlByHash(TEST_HASH);
        assertEquals(TEST_URL, result);
    }

    @Test
    public void testGetUrlByHash_FoundInRepository() {
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(new Url(TEST_HASH, TEST_URL)));

        String result = urlService.getUrlByHash(TEST_HASH);
        assertEquals(TEST_URL, result);
    }

    @Test
    public void testGetUrlByHash_NotFound() {
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> urlService.getUrlByHash(TEST_HASH));
        assertEquals(String.format("Url by hash = %s doesn't exist.", TEST_HASH), thrown.getMessage());
    }

    @Test
    public void testCreateShortUrl() {
        ReflectionTestUtils.setField(urlService, "host", "localhost");
        ReflectionTestUtils.setField(urlService, "port", "8080");

        OriginalUrlRequest request = new OriginalUrlRequest(TEST_URL);
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        String result = urlService.createShortUrl(request);

        assertEquals("http://localhost:8080/" + TEST_HASH, result);
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(any(Url.class));
    }
}
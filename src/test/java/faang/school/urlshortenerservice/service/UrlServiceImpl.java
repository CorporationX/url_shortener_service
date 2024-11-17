package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void getOriginalUrl_ReturnsUrlFromCache_WhenPresentInCache() {
        String hash = "123abc";
        String cachedUrl = "http://example.com";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(cachedUrl));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository, times(1)).get(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getOriginalUrl_ReturnsUrlFromDatabase_WhenNotInCache() {
        String hash = "123abc";
        String dbUrl = "http://example.com";
        Url urlEntity = new Url();
        urlEntity.setHash(hash);
        urlEntity.setUrl(dbUrl);

        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(dbUrl, result);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, times(1)).save(hash, dbUrl);
    }

    @Test
    void getOriginalUrl_ThrowsException_WhenUrlNotFound() {
        String hash = "123abc";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals("URL not found for hash: " + hash, exception.getMessage());

        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }
}
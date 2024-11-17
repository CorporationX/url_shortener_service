package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private final String hash = "exampleHash";
    private final String longUrl = "https://example.com";

    @Test
    void testGetLongUrl_FoundInCache() {
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrl(hash);

        assertThat(result).isEqualTo(longUrl);
        verify(urlCacheRepository).get(hash);
        verify(urlRepository, never()).findByHash(hash);
    }

    @Test
    void testGetLongUrl_FoundInDatabase() {
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrl(hash);

        assertThat(result).isEqualTo(longUrl);
        verify(urlCacheRepository).get(hash);
        verify(urlRepository).findByHash(hash);
        verify(urlCacheRepository).save(hash, longUrl);
    }

    @Test
    void testGetLongUrl_NotFound() {
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrl(hash));

        assertThat(exception.getMessage()).isEqualTo("URL not found for hash: " + hash);
        verify(urlCacheRepository).get(hash);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void testGetShortUrl() {
        String generatedHash = "abc123";
        when(hashCache.getHash()).thenReturn(generatedHash);

        String resultHash = urlService.getShortUrl(longUrl);

        assertEquals(generatedHash, resultHash);

        verify(urlCacheRepository).save(generatedHash, longUrl);

        ArgumentCaptor<Url> captor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(captor.capture());

        Url capturedUrl = captor.getValue();
        assertEquals(generatedHash, capturedUrl.getHash());
        assertEquals(longUrl, capturedUrl.getUrl());
    }
}
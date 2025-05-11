package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NoAvailableHashInCacheException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.hash.HashCache;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void createShortUrl_WhenHashAvailable() {
        String originalUrl = "http://example.com";
        String hash = "abc123";
        UrlRequestDto requestDto = new UrlRequestDto(originalUrl);
        Url url = Url.builder().hash(hash).url(originalUrl).build();
        UrlResponseDto expectedResponse = new UrlResponseDto(hash, originalUrl, LocalDateTime.now());

        when(hashCache.getHash()).thenReturn(hash);

        UrlResponseDto result = urlService.createShortUrl(requestDto);

        assertNotNull(result);
        assertEquals(hash, result.getHash());
        assertEquals(originalUrl, result.getUrl());

        verify(hashCache).getHash();
        verify(hashRepository).save(any(Hash.class));
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(eq(hash), eq(originalUrl));
    }

    @Test
    void createShortUrl_WhenNoHashAvailable() {
        UrlRequestDto requestDto = new UrlRequestDto("http://example.com");
        when(hashCache.getHash()).thenReturn(null);

        assertThrows(NoAvailableHashInCacheException.class, () -> {
            urlService.createShortUrl(requestDto);
        });

        verify(hashCache).getHash();
        verifyNoInteractions(hashRepository, urlRepository, urlCacheRepository);
    }

    @Test
    void getOriginalUrl_WhenUrlInCache() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        UrlResponseDto expectedResponse = new UrlResponseDto(hash, originalUrl, LocalDateTime.now());

        when(urlCacheRepository.get(hash)).thenReturn(originalUrl);

        UrlResponseDto result = urlService.getOriginalUrl(hash);

        assertNotNull(result);
        assertEquals(hash, result.getHash());
        assertEquals(originalUrl, result.getUrl());

        verify(urlCacheRepository).get(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getOriginalUrl_WhenUrlNotInCache() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        Url url = Url.builder().hash(hash).url(originalUrl).build();
        UrlResponseDto expectedResponse = new UrlResponseDto(hash, originalUrl, LocalDateTime.now());

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.of(url));

        UrlResponseDto result = urlService.getOriginalUrl(hash);

        assertNotNull(result);
        assertEquals(hash, result.getHash());
        assertEquals(originalUrl, result.getUrl());

        verify(urlCacheRepository).get(hash);
        verify(urlRepository).findById(hash);
        verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    void getOriginalUrl_WhenUrlNotFound() {
        String hash = "nonexistent";

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> {
            urlService.getOriginalUrl(hash);
        });

        verify(urlCacheRepository).get(hash);
        verify(urlRepository).findById(hash);
        verify(urlCacheRepository, never()).save(any(), any());
    }

    @Test
    void getOriginalUrl_WhenFetchedFromDatabase() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        Url url = Url.builder().hash(hash).url(originalUrl).build();

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.of(url));

        urlService.getOriginalUrl(hash);

        verify(urlCacheRepository).save(hash, originalUrl);
    }
}
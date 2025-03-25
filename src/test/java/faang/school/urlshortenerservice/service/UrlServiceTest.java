package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.publisher.UrlEventPublisher;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlCache urlCache;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlEventPublisher urlEventPublisher;

    @InjectMocks
    private UrlService urlService;

    private String originalUrl;
    private String userId;
    private UrlRequestDto urlRequestDto;


    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://base-url/");
        originalUrl = "http://test.com";
        userId = "user123";
        urlRequestDto = new UrlRequestDto(originalUrl);
    }

    @Test
    public void testCreateShortUrl_UrlExistInCache() {
        String cachedHash = "abc123";
        when(urlCache.getHashByUrl(originalUrl)).thenReturn(cachedHash);

        UrlResponseDto result = urlService.createShortUrl(urlRequestDto, userId);

        assertEquals("http://base-url/abc123", result.getShortUrl());
        verify(urlCache).getHashByUrl(originalUrl);
        verify(urlEventPublisher, times(0)).publishShortUrlCreated(any(), any(), any());
    }

    @Test
    public void testCreateShortUrl_UrlDoesNotExistInCache() {
        String newHash = "xyz789";
        when(urlCache.getHashByUrl(originalUrl)).thenReturn(null);
        when(hashCache.getHash()).thenReturn(newHash);

        UrlResponseDto result = urlService.createShortUrl(urlRequestDto, userId);

        assertEquals("http://base-url/xyz789", result.getShortUrl());
        verify(urlCache).getHashByUrl(originalUrl);
        verify(hashCache).getHash();
        verify(urlCache).saveUrl(any(Url.class));
        verify(urlEventPublisher).publishShortUrlCreated(newHash, originalUrl, userId);
    }

    @Test
    public void testGetUrlFromHash_InCache() {
        String hash = "abc123";
        String expectedUrl = "http://test.com";
        when(urlCache.getUrlByHash(hash)).thenReturn(expectedUrl);

        String result = urlService.getUrlFromHash(hash);

        assertEquals(expectedUrl, result);
        verify(urlCache).getUrlByHash(hash);
        verify(urlRepository, times(0)).findByHash(hash);
    }

    @Test
    public void testGetUrlFromHash_NotInCache() {
        String hash = "abc123";
        when(urlCache.getUrlByHash(hash)).thenThrow(new UrlNotFoundException("URL не найден"));

        assertThrows(UrlNotFoundException.class,
                () -> urlService.getUrlFromHash(hash));

        verify(urlCache).getUrlByHash(hash);
    }

    @Test
    public void testDeleteOldUrl_Success() {
        LocalDateTime fromDate = LocalDateTime.now().minusYears(1);
        Hash hash1 = new Hash("hash1");
        Hash hash2 = new Hash("hash2");
        List<Hash> freeHashes = List.of(hash1, hash2);
        when(urlRepository.removeOldUrlAndGetFreeHashes(fromDate)).thenReturn(freeHashes);

        urlService.deleteOldUrl(fromDate);

        verify(hashRepository).saveAll(freeHashes);
        verify(urlRepository).removeOldUrlAndGetFreeHashes(fromDate);
    }

    @Test
    public void testDeleteOldUrl_NoHashes() {
        LocalDateTime fromDate = LocalDateTime.now().minusYears(1);
        List<Hash> freeHashes = List.of();
        when(urlRepository.removeOldUrlAndGetFreeHashes(fromDate)).thenReturn(freeHashes);

        urlService.deleteOldUrl(fromDate);

        verify(hashRepository, never()).saveAll(anyList());
        verify(urlRepository).removeOldUrlAndGetFreeHashes(fromDate);
    }
}

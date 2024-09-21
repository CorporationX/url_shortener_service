package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private final String baseUrl = "http://localhost:8080/shortener";

    @BeforeEach
    void setup() {
        urlService = new UrlService(urlRepository, urlCacheRepository, hashRepository, hashCache);
        try {
            java.lang.reflect.Field baseUrlField = UrlService.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(urlService, baseUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shortenUrl_ShouldReturnExistingHashFromCache() {
        UrlDto urlDto = new UrlDto("http://example.com");
        String existingHash = "abc123";
        when(urlCacheRepository.getCacheValueByUrl(urlDto.getUrl())).thenReturn(existingHash);

        String result = urlService.shortenUrl(urlDto);

        assertEquals(baseUrl + "/" + existingHash, result);
        verify(urlRepository, never()).findHashByUrl(urlDto.getUrl());
        verify(hashCache, never()).getHash();
        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }

    @Test
    void shortenUrl_ShouldReturnExistingHashFromRepository() {
        UrlDto urlDto = new UrlDto("http://example.com");
        String existingHash = "abc123";
        when(urlCacheRepository.getCacheValueByUrl(urlDto.getUrl())).thenReturn(null);
        when(urlRepository.findHashByUrl(urlDto.getUrl())).thenReturn(existingHash);

        String result = urlService.shortenUrl(urlDto);

        assertEquals(baseUrl + "/" + existingHash, result);
        verify(urlCacheRepository).save(existingHash, urlDto.getUrl());
        verify(hashCache, never()).getHash();
    }

    @Test
    void shortenUrl_ShouldGenerateNewHashAndSave() {
        UrlDto urlDto = new UrlDto("http://example.com");
        String newHash = "xyz456";
        when(urlCacheRepository.getCacheValueByUrl(urlDto.getUrl())).thenReturn(null);
        when(urlRepository.findHashByUrl(urlDto.getUrl())).thenReturn(null);
        when(hashCache.getHash()).thenReturn(newHash);

        String result = urlService.shortenUrl(urlDto);

        assertEquals(baseUrl + "/" + newHash, result);
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(newHash, urlDto.getUrl());
    }

    @Test
    void getOriginalUrl_ShouldReturnUrlFromCache() {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        when(urlCacheRepository.getCacheValue(hash)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlRepository, never()).findById(hash);
    }

    @Test
    void getOriginalUrl_ShouldThrowExceptionIfNotFound() {
        String hash = "abc123";
        when(urlCacheRepository.getCacheValue(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }

    @Test
    void cleanOldUrls_ShouldDeleteOldUrlsAndFreeHashes() {
        Period period = Period.ofDays(30);
        List<String> freedHashes = List.of("abc123", "xyz456");
        when(urlRepository.deleteOldUrlsAndReturnHashes(any(LocalDateTime.class))).thenReturn(freedHashes);

        int freedHashCount = urlService.cleanOldUrls(period);

        assertEquals(freedHashes.size(), freedHashCount);
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void cleanOldUrls_ShouldLogNoOldUrls() {
        Period period = Period.ofDays(30);
        when(urlRepository.deleteOldUrlsAndReturnHashes(any(LocalDateTime.class))).thenReturn(List.of());

        int freedHashCount = urlService.cleanOldUrls(period);

        assertEquals(0, freedHashCount);
        verify(hashRepository, never()).saveAll(anyList());
    }
}

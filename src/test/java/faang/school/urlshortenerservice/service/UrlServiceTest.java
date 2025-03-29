package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.BusinessException;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private HashService hashService;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "serverHost", "localhost");
        ReflectionTestUtils.setField(urlService, "serverPort", "8080");
    }

    @Test
    void shortenUrl_ShouldReturnShortenedUrl() {
        // Arrange
        String originalUrl = "https://example.com";
        String hash = "abc123";
        when(hashCache.getCachedHash()).thenReturn(hash);

        // Act
        UrlReadDto result = urlService.shortenUrl(originalUrl);

        // Assert
        assertEquals("https://localhost:8080/abc123", result.getUrl());

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(urlCaptor.capture());

        Url savedUrl = urlCaptor.getValue();
        assertEquals(hash, savedUrl.getHash());
        assertEquals(originalUrl, savedUrl.getUrl());
    }

    @Test
    void getOriginalUrl_ShouldReturnUrl_WhenHashExists() {
        // Arrange
        String hash = "abc123";
        Url url = new Url(hash, "https://example.com");
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        // Act
        String result = urlService.getOriginalUrl(hash);

        // Assert
        assertEquals(url.getUrl(), result);
    }

    @Test
    void getOriginalUrl_ShouldThrowException_WhenHashNotFound() {
        // Arrange
        String hash = "nonexistent";
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }

    @Test
    void deleteOldUrls_ShouldDeleteUrls_WhenDateIsInPast() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        Url url = new Url("abc123", "https://example.com");
        List<Url> deletedUrls = List.of(url);
        when(urlRepository.deleteOldUrls(pastDate)).thenReturn(deletedUrls);

        // Act
        urlService.deleteOldUrls(pastDate);

        // Assert
        verify(urlRepository).deleteOldUrls(pastDate);
        verify(hashService).saveHashes(List.of("abc123"));
    }

    @Test
    void deleteOldUrls_ShouldThrowException_WhenDateIsInFuture() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        // Act & Assert
        assertThrows(BusinessException.class, () -> urlService.deleteOldUrls(futureDate));
        verify(urlRepository, never()).deleteOldUrls(any());
        verify(hashService, never()).saveHashes(any());
    }
}
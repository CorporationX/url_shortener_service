package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    private static final String TEST_URL = "https://faang-school.com/courses";
    private static final String TEST_HASH = "1urs2eS";
    private UrlDto urlDto;

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Captor
    private ArgumentCaptor<Url> urlCaptor;

    @BeforeEach
    public void setup() {
        urlDto = new UrlDto(TEST_URL);
    }

    @Test
    public void testGenerateShortUrl() {
        // Arrange
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        // Act
        String returnedHash = urlService.generateShortUrl(urlDto);

        // Assert
        verify(urlRepository, times(1)).save(urlCaptor.capture());
        assertEquals(TEST_HASH, returnedHash);
        Url savedUrl = urlCaptor.getValue();
        assertEquals(TEST_URL, savedUrl.getUrl());
        assertEquals(TEST_HASH, savedUrl.getHash());
        assertNotNull(savedUrl.getCreatedAt());
    }

    @Test
    public void testGetUrl_UrlNotFound() {
        // Arrange
        when(urlRepository.findByShortUrl(TEST_HASH)).thenReturn(Optional.empty());
        String message = "Url not found for short link: %s".formatted(TEST_HASH);

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> urlService.getUrl(TEST_HASH));

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGetUrl_Success() {
        // Arrange
        when(urlRepository.findByShortUrl(TEST_HASH)).thenReturn(Optional.of(TEST_URL));

        // Act
        String returnedUrl = urlService.getUrl(TEST_HASH);

        // Assert
        assertEquals(TEST_URL, returnedUrl);
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createShortUrl_ShouldReturnHash_WhenUrlIsSavedSuccessfully() {
        String url = "http://example.com";
        long userId = 123L;
        String hash = "abc123";
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = urlService.createShortUrl(url, userId);

        assertEquals(hash, result);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void getOriginalUrl_ShouldReturnUrl_WhenHashExists() {
        String hash = "abc123";
        long userId = 123L;
        String originalUrl = "http://example.com";
        Url url = Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now())
                .build();
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash, userId);

        assertEquals(originalUrl, result);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void getOriginalUrl_ShouldThrowException_WhenHashDoesNotExist() {
        String hash = "invalidHash";
        long userId = 123L;
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            urlService.getOriginalUrl(hash, userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 NOT_FOUND \"URL not found for hash: " + hash + "\"", exception.getMessage());
        verify(urlRepository, times(1)).findById(hash);
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entiity.Hash;
import faang.school.urlshortenerservice.entiity.Url;
import faang.school.urlshortenerservice.exception.InvalidHashException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.LocalHashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validation.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private LocalHashCache hashCache;
    @Mock
    private UrlValidator urlValidator;
    @Mock
    private UrlVisitService urlVisitService;

    @InjectMocks
    private UrlService urlService;

    @Captor
    private ArgumentCaptor<Url> urlCaptor;

    @Value("${server.host:localhost}:${server.port:8080}")
    private String baseUrl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "localhost:8080");
    }

    @Nested
    @DisplayName("createShortUrl tests")
    class CreateShortUrlTests {
        @Test
        @DisplayName("Should create short URL successfully")
        void shouldCreateShortUrl() {
            String originalUrl = "https://example.com";
            String hashValue = "abc123";
            Hash hash = new Hash(hashValue);
            when(urlValidator.isValid(originalUrl)).thenReturn(true);
            when(hashCache.getNextHash()).thenReturn(hashValue);
            when(hashRepository.findByValueAndUsedFalse(hashValue))
                    .thenReturn(Optional.of(hash));
            String shortUrl = urlService.createShortUrl(originalUrl);
            verify(urlRepository).save(urlCaptor.capture());
            Url savedUrl = urlCaptor.getValue();
            verify(urlCacheRepository).saveUrl(hashValue, originalUrl);
            assertThat(shortUrl).isEqualTo("localhost:8080/" + hashValue);
            assertThat(savedUrl.getOriginalUrl()).isEqualTo(originalUrl);
            assertThat(savedUrl.getHash().getValue()).isEqualTo(hashValue);
        }

        @Test
        @DisplayName("Should throw InvalidUrlException for invalid URL")
        void shouldThrowExceptionForInvalidUrl() {
            String invalidUrl = "invalid-url";
            when(urlValidator.isValid(invalidUrl)).thenReturn(false);
            assertThatThrownBy(() -> urlService.createShortUrl(invalidUrl))
                    .isInstanceOf(InvalidUrlException.class)
                    .hasMessageContaining("Invalid URL format");
        }

        @Test
        @DisplayName("Should throw RuntimeException when hash not found")
        void shouldThrowExceptionWhenHashNotFound() {
            String originalUrl = "https://example.com";
            String hashValue = "abc123";
            when(urlValidator.isValid(originalUrl)).thenReturn(true);
            when(hashCache.getNextHash()).thenReturn(hashValue);
            when(hashRepository.findByValueAndUsedFalse(hashValue))
                    .thenReturn(Optional.empty());
            assertThatThrownBy(() -> urlService.createShortUrl(originalUrl))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Hash not found or already used");
        }
    }

    @Nested
    @DisplayName("redirect tests")
    class RedirectTests {
        @Test
        @DisplayName("Should redirect to original URL successfully")
        void shouldRedirectToOriginalUrl() {
            String hashValue = "abc123";
            String originalUrl = "https://example.com";
            when(urlCacheRepository.getUrl(hashValue)).thenReturn(Optional.of(originalUrl));
            ResponseEntity<Void> response = urlService.redirect(hashValue);
            verify(urlVisitService, timeout(1000)).incrementVisits(hashValue);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation())
                    .isEqualTo(URI.create(originalUrl));
        }

        @Test
        @DisplayName("Should return 404 when URL not found")
        void shouldReturn404WhenUrlNotFound() {
            String hashValue = "abc123";
            when(urlCacheRepository.getUrl(hashValue)).thenReturn(Optional.empty());
            when(urlRepository.findByHashValue(hashValue)).thenReturn(Optional.empty());
            ResponseEntity<Void> response = urlService.redirect(hashValue);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Should throw InvalidHashException for invalid hash")
        void shouldThrowExceptionForInvalidHash() {
            String invalidHash = "abc@123";
            assertThatThrownBy(() -> urlService.redirect(invalidHash))
                    .isInstanceOf(InvalidHashException.class)
                    .hasMessageContaining("Invalid hash format");
        }
    }

    @Nested
    @DisplayName("getOriginalUrl tests")
    class GetOriginalUrlTests {
        @Test
        @DisplayName("Should get URL from cache")
        void shouldGetUrlFromCache() {
            String hashValue = "abc123";
            String originalUrl = "https://example.com";
            when(urlCacheRepository.getUrl(hashValue)).thenReturn(Optional.of(originalUrl));
            String result = urlService.getOriginalUrl(hashValue);
            verify(urlRepository, never()).findByHashValue(any());
            verify(urlVisitService, timeout(1000)).incrementVisits(hashValue);
            assertThat(result).isEqualTo(originalUrl);
        }

        @Test
        @DisplayName("Should get URL from database when not in cache")
        void shouldGetUrlFromDatabase() {
            String hashValue = "abc123";
            String originalUrl = "https://example.com";
            Url url = new Url(new Hash(hashValue), originalUrl);
            when(urlCacheRepository.getUrl(hashValue)).thenReturn(Optional.empty());
            when(urlRepository.findByHashValue(hashValue)).thenReturn(Optional.of(url));
            String result = urlService.getOriginalUrl(hashValue);
            verify(urlCacheRepository).saveUrl(hashValue, originalUrl);
            assertThat(result).isEqualTo(originalUrl);
        }

        @Test
        @DisplayName("Should throw UrlNotFoundException when URL not found")
        void shouldThrowExceptionWhenUrlNotFound() {
            String hashValue = "abc123";
            when(urlCacheRepository.getUrl(hashValue)).thenReturn(Optional.empty());
            when(urlRepository.findByHashValue(hashValue)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> urlService.getOriginalUrl(hashValue))
                    .isInstanceOf(UrlNotFoundException.class)
                    .hasMessageContaining(hashValue);
        }
    }
}
package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.dto.ShortenedUrlResponse;
import faang.school.urlshortenerservice.dto.UrlCreatedEvent;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.UrlMapping;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест UrlService")
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private final String originalUrl = "https://example.com/long-url ";
    private final String hash = "abc123";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(urlService, "urlPrefix", "http://localhost:8080/");
    }

    @Nested
    @DisplayName("Генерация короткой ссылки")
    class ShortenUrlTests {

        @Test
        @DisplayName("Получаем hash, сохраняем и публикуем событие")
        void givenValidHash_whenShortenUrl_thenSavesAndPublishesEvent() {
            ShortenUrlRequest request = new ShortenUrlRequest(originalUrl);
            when(hashCache.getHash()).thenReturn(hash);

            ShortenedUrlResponse response = urlService.shortenUrl(request);

            assertNotNull(response);
            assertEquals("http://localhost:8080/abc123", response.url());

            verify(urlRepository).save(any(UrlMapping.class));
            verify(eventPublisher).publishEvent(any(UrlCreatedEvent.class));
        }
    }

    @Nested
    @DisplayName("Получение оригинального URL")
    class GetOriginalUrlTests {

        @Test
        @DisplayName("URL найден в кэше")
        void givenUrlInCache_whenGetOriginalUrl_thenReturnsFromCache() {
            when(urlCacheRepository.findUrlByHash(hash))
                    .thenReturn(Optional.of(originalUrl));

            String result = urlService.getOriginalUrl(hash);

            assertEquals(originalUrl, result);
            verify(urlRepository, never()).findOriginalUrlByHash(any());
            verify(urlCacheRepository).findUrlByHash(hash);
        }

        @Test
        @DisplayName("URL найден в БД (нет в кэше)")
        void givenUrlNotInCacheButInDatabase_whenGetOriginalUrl_thenReturnsFromDbAndSavesToCache() {
            when(urlCacheRepository.findUrlByHash(hash))
                    .thenReturn(Optional.empty());
            when(urlRepository.findOriginalUrlByHash(hash))
                    .thenReturn(Optional.of(originalUrl));

            String result = urlService.getOriginalUrl(hash);

            assertEquals(originalUrl, result);
            verify(urlRepository).findOriginalUrlByHash(hash);
            verify(eventPublisher).publishEvent(any(UrlCreatedEvent.class));
        }

        @Test
        @DisplayName("URL не найден ни в кэше, ни в БД")
        void givenUrlNotFoundAnywhere_whenGetOriginalUrl_thenThrowsException() {
            when(urlCacheRepository.findUrlByHash(hash))
                    .thenReturn(Optional.empty());
            when(urlRepository.findOriginalUrlByHash(hash))
                    .thenReturn(Optional.empty());

            assertThrows(UrlNotFoundException.class, () ->
                    urlService.getOriginalUrl(hash)
            );
        }
    }
}
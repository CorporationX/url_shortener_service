package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.HashCache;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.JdbcUrlRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String BASE_URL = "http://short.ly";
    private static final String HASH = "abc123";
    private static final String LONG_URL = "https://example.com";

    @Mock
    private HashCache hashCache;

    @Mock
    private JdbcUrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @Captor
    private ArgumentCaptor<LocalDateTime> timeCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", BASE_URL);
    }

    @Test
    @DisplayName("generateShortUrl: хеш генерируется, данные сохраняются и возвращается корректный короткий URL")
    void generateShortUrl_success() {
        when(hashCache.getHash()).thenReturn(HASH);

        String result = urlService.generateShortUrl(LONG_URL);

        assertThat(result).isEqualTo(BASE_URL + "/" + HASH);

        verify(urlRepository).save(eq(HASH), eq(LONG_URL), timeCaptor.capture());
        verify(urlCacheRepository).putUrl(HASH, LONG_URL);
        assertThat(timeCaptor.getValue()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Nested
    @DisplayName("getLongUrl:")
    class GetLongUrl {

        @Test
        @DisplayName("значение берётся из кеша, БД не вызывается")
        void fromCache() {
            when(urlCacheRepository.getUrl(HASH)).thenReturn(LONG_URL);

            String result = urlService.getLongUrl(HASH);

            assertThat(result).isEqualTo(LONG_URL);

            verify(urlRepository, never()).findUrlByHash(anyString());
            verify(urlCacheRepository, never()).putUrl(anyString(), anyString());
        }

        @Test
        @DisplayName("значение берётся из БД и кладётся в кеш")
        void fromDatabase() {
            when(urlCacheRepository.getUrl(HASH)).thenReturn(null);
            when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.of(LONG_URL));

            String result = urlService.getLongUrl(HASH);

            assertThat(result).isEqualTo(LONG_URL);

            verify(urlRepository).findUrlByHash(HASH);
            verify(urlCacheRepository).putUrl(HASH, LONG_URL);
        }

        @Test
        @DisplayName("при отсутствии значения везде выбрасывается UrlNotFoundException")
        void notFound() {
            when(urlCacheRepository.getUrl(HASH)).thenReturn(null);
            when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> urlService.getLongUrl(HASH))
                    .isInstanceOf(UrlNotFoundException.class)
                    .hasMessageContaining(HASH);

            verify(urlCacheRepository, never()).putUrl(anyString(), anyString());
        }
    }
}
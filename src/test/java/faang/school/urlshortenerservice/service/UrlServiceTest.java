package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.handler.UrlNotFoundException;
import faang.school.urlshortenerservice.properties.ShortenerProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private ShortenerProperties properties;

    @InjectMocks
    UrlService urlService;

    private final String originalUrl = "http://example.com";
    private final String baseUrl = "http://localhost:8080";
    private final String hash = "abc123";
    private final String shortUrl = baseUrl + "/" + hash;

    @Nested
    class CreateShortUrl {
        @Test
        void returnExistingShortUrl() {
            Url existing = new Url();
            existing.setHash(hash);
            existing.setUrl(originalUrl);

            when(urlRepository.findByUrl(originalUrl)).thenReturn(Optional.of(existing));
            when(properties.getBaseUrl()).thenReturn(URI.create(baseUrl));

            String result = urlService.createShortUrl(originalUrl);

            assertThat(result).isEqualTo(shortUrl);
            verify(urlRepository, never()).save(any());
        }

        @Test
        void newShortUrl() {
            when(urlRepository.findByUrl(originalUrl)).thenReturn(Optional.empty());
            when(hashCache.getHash()).thenReturn(hash);
            when(properties.getBaseUrl()).thenReturn(URI.create(baseUrl));

            String result = urlService.createShortUrl(originalUrl);

            assertThat(result).isEqualTo(shortUrl);
            verify(urlRepository).save(any(Url.class));
            verify(urlCacheRepository).save(eq(hash), eq(originalUrl));
        }
    }

    @Nested
    class ResolveUrl {
        @Test
        void returnFromCache() {
            when(urlCacheRepository.find(hash)).thenReturn(Optional.of(originalUrl));

            String result = urlService.resolveUrl(hash);

            assertThat(result).isEqualTo(originalUrl);
            verify(urlRepository, never()).findById(any());
        }

        @Test
        void returnFromDbAndCache() {
            Url entity = new Url();
            entity.setUrl(originalUrl);
            when(urlCacheRepository.find(hash)).thenReturn(Optional.empty());
            when(urlRepository.findById(hash)).thenReturn(Optional.of(entity));

            String result = urlService.resolveUrl(hash);

            assertThat(result).isEqualTo(originalUrl);
            verify(urlCacheRepository).save(hash, originalUrl);
        }

        @Test
        void urlNotFoundException() {
            when(urlCacheRepository.find(hash)).thenReturn(Optional.empty());
            when(urlRepository.findById(hash)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> urlService.resolveUrl(hash))
                    .isInstanceOf(UrlNotFoundException.class)
                    .hasMessageContaining("URL not found");
        }
    }

    @Nested
    class CreateOrResolveUrl {
        @Test
        void resolve() {
            String input = baseUrl + "/" + hash;

            when(properties.getBaseUrl()).thenReturn(URI.create(baseUrl));
            when(urlCacheRepository.find(hash)).thenReturn(Optional.of(originalUrl));

            String result = urlService.createOrResolveUrl(input);

            assertThat(result).isEqualTo(originalUrl);
        }

        @Test
        void create() {
            when(urlRepository.findByUrl(originalUrl)).thenReturn(Optional.empty());
            when(hashCache.getHash()).thenReturn(hash);
            when(properties.getBaseUrl()).thenReturn(URI.create(baseUrl));

            String result = urlService.createOrResolveUrl(originalUrl);

            assertThat(result).isEqualTo(shortUrl);
        }
    }
}
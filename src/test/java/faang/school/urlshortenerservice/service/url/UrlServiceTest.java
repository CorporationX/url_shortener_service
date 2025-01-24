package faang.school.urlshortenerservice.service.url;


import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.util.HashCache;
import faang.school.urlshortenerservice.util.UrlBuilder;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    private static final long URL_TTL = 24;
    private static final String REQUEST_URL = "google.com";
    private static final String HASH = "hash";
    private static final String RESPONSE_URL = "sh.com/hash";
    private static final Url URL = Url.builder()
            .hash(HASH)
            .url(REQUEST_URL)
            .build();

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlBuilder urlBuilder;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "urlTtlInCache", URL_TTL);
    }

    @Test
    void testCreateHashUrl_successful() {
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlBuilder.response(HASH)).thenReturn(RESPONSE_URL);

        assertThat(urlService.createHashUrl(REQUEST_URL))
                .isNotNull()
                .isEqualTo(RESPONSE_URL);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);

        verify(urlRepository).save(urlCaptor.capture());
        verify(urlCacheRepository).saveByTtlInHour(urlCaptor.capture(), ttlCaptor.capture());

        Url expectedUrl = URL;

        assertThat(urlCaptor.getAllValues().get(0))
                .usingRecursiveComparison()
                .isEqualTo(expectedUrl);
        assertThat(urlCaptor.getAllValues().get(1))
                .usingRecursiveComparison()
                .isEqualTo(expectedUrl);
        assertThat(ttlCaptor.getValue())
                .isEqualTo(URL_TTL);

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);

        verify(urlBuilder).response(hashCaptor.capture());

        assertThat(hashCaptor.getValue())
                .isEqualTo(HASH);
    }

    @Test
    void testGetPrimalUri_cacheHaveUrl() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(Optional.of(URL));

        assertThat(urlService.getPrimalUri(HASH))
                .isNotNull()
                .isEqualTo(REQUEST_URL);

        verify(urlRepository, never()).findById(anyString());
        verify(urlCacheRepository, never()).saveByTtlInHour(any(Url.class), any(Long.class));
    }

    @Test
    void testGetPrimalUrl_noUrlInTheCache() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(HASH)).thenReturn(Optional.of(URL));

        assertThat(urlService.getPrimalUri(HASH))
                .isNotNull()
                .isEqualTo(REQUEST_URL);

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);

        verify(urlRepository).findById(hashCaptor.capture());
        verify(urlCacheRepository).saveByTtlInHour(urlCaptor.capture(), ttlCaptor.capture());

        assertThat(hashCaptor.getValue())
                .isNotNull()
                .isEqualTo(HASH);
        assertThat(urlCaptor.getValue())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(URL);
        assertThat(ttlCaptor.getValue())
                .isEqualTo(URL_TTL);
    }

    @Test
    void testGetPrimalUrl_exception() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getPrimalUri(HASH))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Url not found for hash: " + HASH);

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);

        verify(urlRepository).findById(hashCaptor.capture());
        verify(urlCacheRepository, never()).saveByTtlInHour(any(Url.class), any(Long.class));

        assertThat(hashCaptor.getValue())
                .isNotNull()
                .isEqualTo(HASH);
    }
}

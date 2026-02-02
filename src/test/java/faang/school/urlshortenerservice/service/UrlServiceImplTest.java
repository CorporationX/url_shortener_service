package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    private static final String TEST_HASH = "abc123";
    private static final String TEST_URL = "https://example.com";

    @Test
    void testCreateShortUrl() {
        when(hashCache.getHash()).thenReturn(TEST_HASH);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = urlService.createShortUrl(TEST_URL);

        assertThat(result).isEqualTo(TEST_HASH);
        verify(urlRepository).save(argThat(url ->
                url.getHash().equals(TEST_HASH) && url.getOriginalUrl().equals(TEST_URL)
        ));
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
    }

    @Test
    void testGetOriginalUrlFromCache() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.of(TEST_URL));

        String result = urlService.getOriginalUrl(TEST_HASH);

        assertThat(result).isEqualTo(TEST_URL);
        verify(urlRepository, never()).findById(anyString());
    }

    @Test
    void testGetOriginalUrlFromDatabase() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());
        
        Url urlEntity = Url.builder()
                .hash(TEST_HASH)
                .originalUrl(TEST_URL)
                .build();
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getOriginalUrl(TEST_HASH);

        assertThat(result).isEqualTo(TEST_URL);
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
    }

    @Test
    void testGetOriginalUrlNotFound() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getOriginalUrl(TEST_HASH))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining(TEST_HASH);
    }
}



package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private static final String HASH = "hash";
    private static final String URL = "url";
    private Url url;

    @BeforeEach
    public void init() {
        url = Url.builder()
                .hash(HASH)
                .url(URL)
                .build();
    }

    @Test
    @DisplayName("Success when get long url by urlRepository")
    public void whenGetLongUrlByUrlRepositoryThenReturnLongUrl() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(HASH);

        assertNotNull(result);
        assertEquals(URL, result);
        verify(urlCacheRepository).findUrlByHash(HASH);
        verify(urlRepository).findUrlByHash(HASH);
    }

    @Test
    @DisplayName("Success when get long url by urlCacheRepository")
    public void whenGetLongUrlByUrlCacheRepositoryThenReturnLongUrl() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(HASH);

        assertNotNull(result);
        assertEquals(URL, result);
        verify(urlCacheRepository).findUrlByHash(HASH);
    }

    @Test
    @DisplayName("Exception when get long url")
    public void whenGetLongUrlThenThrowException() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getLongUrl(HASH));
    }
}
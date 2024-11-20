package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
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
    private HashRepository hashRepository;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private static final String HASH = "hash";
    private static final String URL = "url";
    private List<String> existingHashes;
    private Url url;

    @BeforeEach
    public void init() {
        url = Url.builder()
                .hash(HASH)
                .url(URL)
                .build();
        existingHashes = Arrays.asList(HASH);
        ReflectionTestUtils.setField(cacheProperties, "nonWorkingUrlTime", 1);

    }

    @Test
    @DisplayName("Success when clean DB")
    public void whenCleanDBThenDeleteOldUrlAndGetHashes() {
        when(urlRepository.getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime())).thenReturn(existingHashes);
        doNothing().when(hashRepository).saveAllHashesBatched(anyList());

        urlService.releaseExpiredHashes();

        verify(urlRepository).getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime());
        verify(hashRepository).saveAllHashesBatched(anyList());
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
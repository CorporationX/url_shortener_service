package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.enity.Url;
import faang.school.urlshortenerservice.hash.LocalHash;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    private UrlServiceImpl urlService;

    @Mock
    private LocalHash localHash;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashService hashService;

    @Mock
    private CacheManager cacheManager;

    private final HashProperties hashProperties = new HashProperties();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String url1 = "http://site.com";
    private final String hash = "JHJ48";
    private final Url url = Url.builder().url(url1).build();
    private final List<Url> urls = List.of(url);

    @BeforeEach
    void setUp() {
        urlService = new UrlServiceImpl(localHash, urlRepository, hashService, cacheManager, hashProperties, executor);
    }

    @Test
    void save() {
        hashProperties.setCaches(new HashProperties.Caches("cache"));
        when(localHash.getHash()).thenReturn(hash);
        when(cacheManager.getCache(hashProperties.getCaches().getHashToUrl())).thenReturn(null);

        HashDto hashDto = urlService.save(url1);

        assertEquals(hash, hashDto.getHash());
        verify(localHash, times(1)).getHash();
        verify(urlRepository, times(1)).save(any());
        verify(cacheManager, times(1)).getCache(hashProperties.getCaches().getHashToUrl());
    }


    @Test
    void get() {
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));
        String url = urlService.get(hash);
        assertEquals(url1, url);
    }

    @Test
    void get_ShouldNotGetWhenUrlNotExists() {
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> urlService.get(hash));
    }

    @Test
    void freeUnusedHash() {
        hashProperties.setGet(HashProperties.Get.builder().build());
        hashProperties.setSaving(new HashProperties.Saving(10, Duration.ofDays(5)));

        when(urlRepository.deleteAndGetUnusedUrl(any(LocalDateTime.class), anyInt())).thenReturn(urls);

        assertDoesNotThrow(() -> urlService.freeUnusedHash());
        verify(urlRepository, times(1)).deleteAndGetUnusedUrl(any(LocalDateTime.class), anyInt());
        verify(hashService, times(1)).saveAll(any(List.class));
    }
}
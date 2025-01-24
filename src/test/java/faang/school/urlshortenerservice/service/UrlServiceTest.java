package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.TemporarilyUnavailableException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private final static String originalUrl = "http://baeldung.com";
    private final static String hash = "hashabc";
    private final String baseUrl = "http://localhost:8080/shortly-az";

    @BeforeEach
    void setUp() {
        try {
            java.lang.reflect.Field baseUrlField = UrlService.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(urlService, baseUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createShortUrlSuccessTest() {
        UrlDto urlDto = getUrlDto();
        Url url = getUrlEntity();

        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any())).thenReturn(url);

        String result = urlService.createShortUrl(urlDto);

        assertEquals(baseUrl + "/" + hash, result);
        verify(urlCacheRepository).save(hash, urlDto.getUrl());
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any());
    }

    @Test
    void createShortUrlFailTest() {
        UrlDto urlDto = getUrlDto();

        Exception exception = assertThrows(TemporarilyUnavailableException.class, () -> urlService.createShortUrl(urlDto));
        assertEquals(UrlService.FAILED_TO_GENERATE_SHORT_LINK, exception.getMessage());
    }

    @Test
    void getOriginalUrlSuccessTest() {
        when(urlCacheRepository.getCacheValueByHash(hash)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlRepository, never()).findById(hash);
    }

    @Test
    void getOriginalUrlNotFoundFailTest() {
        when(urlCacheRepository.getCacheValueByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }

    @Test
    void cleanUrlsSuccessTest() {
        Period period = Period.ofDays(5);
        List<String> freedHashes = List.of(hash, "123123");
        when(urlRepository.deleteUrlsAndReturnHashList(any(LocalDateTime.class))).thenReturn(freedHashes);

        long actualSize = urlService.cleanUrls(period);

        assertEquals(freedHashes.size(), actualSize);
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void cleanUrlsFailTest() {
        Period period = Period.ofDays(5);
        when(urlRepository.deleteUrlsAndReturnHashList(any(LocalDateTime.class))).thenReturn(List.of());

        long actualSize = urlService.cleanUrls(period);

        assertEquals(0, actualSize);
        verify(hashRepository, never()).saveAll(anyList());
    }


    private Url getUrlEntity() {
        return Url.builder()
                .hash(hash)
                .url(originalUrl)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private UrlDto getUrlDto() {
        return new UrlDto(originalUrl);
    }
}
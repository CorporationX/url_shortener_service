package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UrlService urlService;

    @Captor
    private ArgumentCaptor<List<Long>> randomNumbersCaptor;

    private final String baseUrl = "http://localhost:18080";
    private final int hashCount = 5;
    private final Long userId = 123L;
    private final String originalUrl = "https://www.example.com";
    private final String hash = "abc123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", baseUrl);
        ReflectionTestUtils.setField(urlService, "hashCount", hashCount);
    }

    @Test
    void generateShortUrl_Success() {
        Url url = new Url();
        url.setUrl(originalUrl);

        when(userContext.getUserId()).thenReturn(userId);
        when(hashCache.getHashCache(anyList())).thenReturn(List.of(hash));
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Url result = urlService.generateShortUrl(url);

        assertEquals(userId, result.getUserId());
        assertEquals(baseUrl + hash, result.getShortUrl());
        assertEquals(originalUrl, result.getUrl());

        verify(hashCache).getHashCache(randomNumbersCaptor.capture());
        assertEquals(hashCount, randomNumbersCaptor.getValue().size());

        verify(urlCacheRepository).saveUrl(hash, originalUrl);
        verify(urlRepository).save(url);
    }

    @Test
    void generateShortUrl_EmptyHashList_ThrowsException() {
        Url url = new Url();
        url.setUrl(originalUrl);

        when(userContext.getUserId()).thenReturn(userId);
        when(hashCache.getHashCache(anyList())).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urlService.generateShortUrl(url));

        assertEquals("Failed to generate hash for URL", exception.getMessage());
        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).saveUrl(any(), any());
    }

    @Test
    void generateShortUrl_ValidatesUniqueRandomNumbers() {
        Url url = new Url();
        url.setUrl(originalUrl);

        when(userContext.getUserId()).thenReturn(userId);
        when(hashCache.getHashCache(anyList())).thenReturn(List.of(hash));
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        urlService.generateShortUrl(url);

        verify(hashCache).getHashCache(randomNumbersCaptor.capture());
        List<Long> randomNumbers = randomNumbersCaptor.getValue();

        assertEquals(hashCount, randomNumbers.size());

        assertEquals(randomNumbers.size(), randomNumbers.stream().distinct().count());
    }
}

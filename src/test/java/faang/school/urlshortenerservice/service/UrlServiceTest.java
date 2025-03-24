package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private HashRepository hashRepository;

    @InjectMocks
    private UrlService urlService;

    private final String testDomain = "https://short.url/";
    private final String testHash = "abc123";
    private final String testLongUrl = "https://example.com/very/long/url";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "domain", testDomain);
    }

    @Test
    @DisplayName("Создание короткой ссылки: возвращает корректный ответ")
    void createShortUrlReturnsCorrectResponse() {
        when(hashCache.getHash()).thenReturn(testHash);
        when(urlRepository.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0));

        UrlResponseDto response = urlService.createShortUrl(testLongUrl);

        assertNotNull(response);
        assertEquals(testLongUrl, response.originalUrl());
        assertEquals(testDomain + testHash, response.shortUrl());

        verify(hashCache).getHash();
        verify(urlRepository).save(argThat(url ->
                url.getHash().equals(testHash) && url.getUrl().equals(testLongUrl)));
        verify(urlCacheRepository).save(testHash, testLongUrl);
    }

    @Test
    @DisplayName("Получение оригинальной ссылки: возвращает из кэша")
    void getOriginalUrlReturnsFromCache() {
        when(urlCacheRepository.find(testHash)).thenReturn(testLongUrl);

        String result = urlService.getOriginalUrl(testHash);

        assertEquals(testLongUrl, result);
        verify(urlCacheRepository).find(testHash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    @DisplayName("Получение оригинальной ссылки: возвращает из репозитория при отсутствии в кэше")
    void getOriginalUrlReturnsFromRepositoryWhenNotInCache() {
        when(urlCacheRepository.find(testHash)).thenReturn(null);
        when(urlRepository.findById(testHash))
                .thenReturn(Optional.of(Url.builder().hash(testHash).url(testLongUrl).build()));

        String result = urlService.getOriginalUrl(testHash);

        assertEquals(testLongUrl, result);
        verify(urlCacheRepository).find(testHash);
        verify(urlRepository).findById(testHash);
    }

    @Test
    @DisplayName("Получение оригинальной ссылки: выбрасывает исключение при отсутствии ссылки")
    void getOriginalUrlThrowsExceptionWhenUrlNotFound() {
        when(urlCacheRepository.find(testHash)).thenReturn(null);
        when(urlRepository.findById(testHash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(testHash));
        verify(urlCacheRepository).find(testHash);
        verify(urlRepository).findById(testHash);
    }

    @Test
    @DisplayName("Удаление старых URL: корректно удаляет и сохраняет хэши")
    void removeOldUrlsDeletesAndSavesHashes() {
        LocalDateTime cutoffDate = LocalDateTime.now();
        List<String> oldHashes = List.of(testHash, "def456");

        when(urlRepository.deleteOldUrlsAndReturnHashes(cutoffDate)).thenReturn(oldHashes);

        urlService.removeOldUrls(cutoffDate);

        verify(urlRepository).deleteOldUrlsAndReturnHashes(cutoffDate);
        verify(hashRepository, times(oldHashes.size())).save(any(Hash.class));
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlServiceTest {
    @Spy
    private UrlMapperImpl urlMapper;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Метод для проверки успешной генерации короткой ссылки
    @Test
    void testGetShortenedUrl_SuccessfulSave() {
        RequestUrlDto dto = new RequestUrlDto("https://example.com");

        when(hashCache.getHash()).thenReturn("abc123");
        when(urlRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseUrlDto result = urlService.getShortenedUrl(dto);

        assertEquals("abc123", result.getHash()); // проверяем, что хэш сохранён правильно
        verify(urlCacheRepository, times(1))
                .cacheHash(eq("abc123"),
                        eq("https://example.com"),
                        anyInt()); // проверка вызова метода кэширования
    }

    @Test
    void testGetShortenedUrl_DuplicateKey() {
        RequestUrlDto dto = new RequestUrlDto("https://example.com");

        when(hashCache.getHash()).thenReturn("abc123");
        DataIntegrityViolationException diException = new DataIntegrityViolationException("Duplicate URL found.",
                new IllegalStateException("duplicate key value violates unique constraint \"url_url_key\""));
        doThrow(diException).when(urlRepository).save(any());
        when(urlRepository.getHashByUrl("https://example.com")).thenReturn("abc123"); // существующий хэш

        ResponseUrlDto result = urlService.getShortenedUrl(dto);

        assertEquals("abc123", result.getHash()); // проверяем, что используется старый хэш
    }

    // Тестирование получения полной ссылки по правильному хэшу
    @Test
    void testGetLongUrlByHash_CachedUrl() {
        String hash = "abc123";
        String longUrl = "https://example.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn(longUrl);

        String result = urlService.getLongUrlByHash(hash);

        assertEquals(longUrl, result); // проверяем совпадение полученной ссылки
        verify(urlRepository, never()).getLongUrlByHash(anyString()); // доступ к БД не выполнялся
    }

    // Тестирование получения полной ссылки по отсутствующему хэшу в кэше
    @Test
    void testGetLongUrlByHash_NotCachedButExistsInDB() {
        String hash = "abc123";
        String longUrl = "https://example.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.getLongUrlByHash(hash)).thenReturn(longUrl);

        String result = urlService.getLongUrlByHash(hash);

        assertEquals(longUrl, result); // проверяем совпадение полученной ссылки
        verify(urlCacheRepository, times(1)).getUrl(hash); // обращение к кэшу было выполнено
        verify(urlRepository, times(1)).getLongUrlByHash(hash); // обращение к БД также произошло
    }

    // Тестирование исключения при отсутствии ссылки в БД
    @Test
    void testGetLongUrlByHash_UrlNotFound() {
        String hash = "abc123";

        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.getLongUrlByHash(hash)).thenReturn(null);

        assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrlByHash(hash)); // ожидаем исключение
    }
}

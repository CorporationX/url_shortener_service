package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.UrlLifeTimeConfig;
import faang.school.urlshortenerservice.exception.DuplicateUrlException;
import faang.school.urlshortenerservice.exception.UrlExpiredException;
import faang.school.urlshortenerservice.model.dto.UrlRequestDto;
import faang.school.urlshortenerservice.model.dto.UrlResponseDto;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.model.enums.UrlStatus;
import faang.school.urlshortenerservice.producer.RabbitQueueProducerService;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String PATH_WITH_HASHED_URL = "http://localhost:8080/url/";
    private static final String LONG_URL = "https://faang-school.com/courses";
    private static final String HASH = "123abc";

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private RabbitQueueProducerService rabbitQueueProducerService;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "pathWithHashedUrl", PATH_WITH_HASHED_URL);
        UrlLifeTimeConfig lifeTimeConfig = UrlLifeTimeConfig.builder().months(12).days(0).hours(0).build();
        ReflectionTestUtils.setField(urlService, "lifeTime", lifeTimeConfig);
    }

    @Test
    void testGenerateShortUrl() {
        UrlRequestDto requestDto = UrlRequestDto.builder().longUrl(LONG_URL).build();
        when(urlRepository.findByUrl(LONG_URL)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setHash(HASH);
            return url;
        });
        doNothing().when(urlCacheRepository).save(any(Url.class));
        doNothing().when(rabbitQueueProducerService).sendUrlIdForValidation(eq(HASH));
        UrlResponseDto response = urlService.generateShortUrl(requestDto);
        assertNotNull(response);
        assertEquals(PATH_WITH_HASHED_URL + HASH, response.getShortUrl());
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(any(Url.class));
        verify(rabbitQueueProducerService).sendUrlIdForValidation(eq(HASH));
    }

    @Test
    void testGenerateShortUrl_duplicateUrl() {
        UrlRequestDto requestDto = UrlRequestDto.builder().longUrl(LONG_URL).build();
        Url existingUrl = new Url(LONG_URL, HASH, UrlStatus.OK, LocalDateTime.now().plusHours(1));
        when(urlRepository.findByUrl(LONG_URL)).thenReturn(Optional.of(existingUrl));

        DuplicateUrlException exception = assertThrows(DuplicateUrlException.class, () -> {
            urlService.generateShortUrl(requestDto);
        });

        assertNotNull(exception);
        assertEquals("URL already exists in the database.", exception.getMessage());
        verify(urlRepository, never()).save(any(Url.class));
        verify(rabbitQueueProducerService, never()).sendUrlIdForValidation(anyString());
    }

    @Test
    void testGetUrlByHash_FromRedis() {
        Url cachedUrl = Url.builder().hash(HASH).url(LONG_URL).build();
        when(urlCacheRepository.findByHash(HASH)).thenReturn(Optional.of(cachedUrl));
        String result = urlService.getUrlByHash(HASH);
        assertEquals(LONG_URL, result);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void testGetUrlByHash_FromPostgres() {
        Url dbUrl = Url.builder().hash(HASH).url(LONG_URL).build();
        when(urlCacheRepository.findByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(dbUrl));
        String result = urlService.getUrlByHash(HASH);
        assertEquals(LONG_URL, result);
    }

    @Test
    void testGetUrlByHash_UrlExpired() {
        when(urlCacheRepository.findByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());
        assertThrows(UrlExpiredException.class, () -> urlService.getUrlByHash(HASH));
    }

    @Test
    void testCleanHashes() {
        List<String> cleanedHashes = List.of("1", "abc");
        when(urlRepository.getOldUrlsAndDelete()).thenReturn(cleanedHashes);
        List<String> result = urlService.cleanHashes();
        assertEquals(cleanedHashes, result);
    }
}

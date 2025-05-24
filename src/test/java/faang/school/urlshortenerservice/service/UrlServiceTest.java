package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
import faang.school.urlshortenerservice.exception.ShortUrlNotFoundException;
import faang.school.urlshortenerservice.hash_generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlRedis;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlRedisRepository urlRedisRepository;

    @Spy
    private UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private static final String BASE_ADDRESS_FIELD_NAME = "baseAddress";
    private static final String TEST_URL = "https://example.com/";
    private static final String BASE_ADDRESS = "https://sh.com/";
    private static final String TEST_HASH = "abc123";
    private static final String SHORT_LINK = BASE_ADDRESS + TEST_HASH;
    private static final long TEST_TTL = 86400L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, BASE_ADDRESS_FIELD_NAME, BASE_ADDRESS);
        ReflectionTestUtils.setField(urlService, "hashTtl", TEST_TTL);
    }

    @Test
    void getShortUrl_shouldCreateAndSaveUrlAndReturnShortUrl() {
        // Arrange
        var requestDto = new ShortUrlRequestDto(TEST_URL);
        var urlRedis = new UrlRedis(TEST_HASH, TEST_URL, TEST_TTL);

        when(hashCache.getHash()).thenReturn(TEST_HASH);

        // Act
        var result = urlService.getShortUrl(requestDto);

        // Assert
        assertEquals(BASE_ADDRESS + TEST_HASH, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(argThat(url ->
                url.getUrl().equals(TEST_URL) &&
                        url.getHash().equals(TEST_HASH)
        ));
        verify(urlMapper).toUrlRedis(any(Url.class));
        verify(urlRedisRepository).save(urlRedis);
    }

    @Test
    void getShortUrl_whenRedisThrowsException_shouldHandleGracefully() {
        // Arrange
        var requestDto = new ShortUrlRequestDto(TEST_URL);

        when(hashCache.getHash()).thenReturn(TEST_HASH);
        doThrow(new RuntimeException("Redis connection error")).when(urlRedisRepository).save(any(UrlRedis.class));

        // Act
        var result = urlService.getShortUrl(requestDto);

        // Assert
        assertEquals(BASE_ADDRESS + TEST_HASH, result);
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void getShortUrl_shouldUseConfiguredBaseAddress() {
        // Arrange
        var customBaseAddress = "https://custom.com/";
        ReflectionTestUtils.setField(urlService, BASE_ADDRESS_FIELD_NAME, customBaseAddress);

        var requestDto = new ShortUrlRequestDto(TEST_URL);
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        // Act
        var result = urlService.getShortUrl(requestDto);

        // Assert
        assertEquals(customBaseAddress + TEST_HASH, result);
    }

    @Test
    void getShortUrl_shouldPassCorrectEntityToMapper() {
        var requestDto = new ShortUrlRequestDto(TEST_URL);
        when(hashCache.getHash()).thenReturn(TEST_HASH);
        urlService.getShortUrl(requestDto);

        verify(urlMapper).toUrlRedis(argThat(url ->
                url.getUrl().equals(TEST_URL) &&
                        url.getHash().equals(TEST_HASH)
        ));
    }

    @Test
    void redirectToOriginalUrl_FoundInRedis_ReturnsUrl() {
        // Arrange
        var redisEntity = new UrlRedis();
        redisEntity.setUrl(TEST_URL);

        when(urlRedisRepository.findById(TEST_HASH)).thenReturn(Optional.of(redisEntity));

        // Act
        var result = urlService.redirectToOriginalUrl(SHORT_LINK);

        // Assert
        assertEquals(TEST_URL, result);
        verify(urlRedisRepository).findById(TEST_HASH);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void redirectToOriginalUrl_NotFoundInRedisButFoundInRepo_ReturnsUrl() {
        // Arrange
        when(urlRedisRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

        var urlEntity = new Url();
        urlEntity.setUrl(TEST_URL);

        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(urlEntity));

        // Act
        var result = urlService.redirectToOriginalUrl(SHORT_LINK);

        // Assert
        assertEquals(TEST_URL, result);
        verify(urlRedisRepository).findById(TEST_HASH);
        verify(urlRepository).findById(TEST_HASH);
    }

    @Test
    void redirectToOriginalUrl_RedisThrowsException_FallsBackToRepo() {
        // Arrange
        when(urlRedisRepository.findById(TEST_HASH)).thenThrow(new RuntimeException("Redis error"));

        var urlEntity = new Url();
        urlEntity.setUrl(TEST_URL);

        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.of(urlEntity));

        // Act
        var result = urlService.redirectToOriginalUrl(SHORT_LINK);

        // Assert
        assertEquals(TEST_URL, result);
        verify(urlRedisRepository).findById(TEST_HASH);
        verify(urlRepository).findById(TEST_HASH);
    }

    @Test
    void redirectToOriginalUrl_NotFoundAnywhere_ThrowsException() {
        // Arrange
        when(urlRedisRepository.findById(TEST_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(TEST_HASH)).thenReturn(Optional.empty());

        // Act + Assert
        var exception = assertThrows(
                ShortUrlNotFoundException.class,
                () -> urlService.redirectToOriginalUrl(SHORT_LINK)
        );

        assertTrue(exception.getMessage().contains(SHORT_LINK));
        verify(urlRedisRepository).findById(TEST_HASH);
        verify(urlRepository).findById(TEST_HASH);
    }
}
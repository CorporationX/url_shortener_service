package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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

    private final String TEST_HASH = "abc123";
    private final String TEST_URL = "https://example.com/";
    private final String BASE_ADDRESS = "https://sh.com/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseAddress", BASE_ADDRESS);
    }

    @Test
    void getShortUrl_shouldCreateAndSaveUrlAndReturnShortUrl() {
        // Arrange
        var requestDto = new ShortUrlRequestDto(TEST_URL);
        var urlRedis = new UrlRedis(TEST_HASH, TEST_URL);

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
        ReflectionTestUtils.setField(urlService, "baseAddress", customBaseAddress);

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
}
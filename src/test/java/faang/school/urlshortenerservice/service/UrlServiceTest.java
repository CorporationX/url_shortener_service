package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exceptions.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.utils.HashCache;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    private Validator validator;
    private String testUrl;
    private String testHash;
    private UrlDto testDto;
    private Url mockUrl;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();

        testUrl = "https://google.com";
        testHash = "hash";
        testDto = new UrlDto(testUrl, null, null);

        mockUrl = new Url();
        mockUrl.setUrl(testUrl);
        mockUrl.setHash(testHash);

        lenient().when(hashCache.getHashFromCache()).thenReturn(testHash);
        lenient().when(urlMapper.toEntity(any(UrlDto.class))).thenReturn(mockUrl);
    }

    @Test
    public void testIncorrectUrl() {
        UrlDto invalidDto = new UrlDto("123", null, null);

        assertThrows(ConstraintViolationException.class, () -> {
                var violations = validator.validate(invalidDto);
                if(!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }
            });
    }

    @Test
    public void testSavesToCache() {
        urlService.getShortUrl(testDto);

        verify(urlCacheRepository, times(1)).save(testHash, testUrl);
    }

    @Test
    public void testSavesToDb() {
        urlService.getShortUrl(testDto);

        verify(urlCacheRepository, times(1)).save(testHash, testUrl);
        verify(urlRepository, times(1)).save(mockUrl);
        verify(urlMapper, times(1)).toEntity(any(UrlDto.class));
    }

    @Test
    public void testReturnsCorrectString() {
        String result = urlService.getShortUrl(testDto);

        assertEquals("https://localhost:8080/url/" + testHash, result);
    }

    @Test
    public void testHashIsInCache() {
        when(urlCacheRepository.findByHashInRedis(testHash)).thenReturn(testHash);
        String result = urlService.redirectToRealUrl(testHash);

        assertEquals("hash", result);
    }

    @Test
    public void testHashIsNotInCacheOrDb() {
        when(urlCacheRepository.findByHashInRedis(testHash)).thenReturn(null);
        when(urlRepository.findByHash(testHash)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class, () -> urlService.redirectToRealUrl(testHash));

        assertEquals("Url not found for hash " + testHash, exception.getMessage());
    }

    @Test
    public void testHashIsInDb() {
        when(urlCacheRepository.findByHashInRedis(testHash)).thenReturn(null);
        when(urlRepository.findByHash(testHash)).thenReturn(Optional.of(mockUrl));

        String result = urlService.redirectToRealUrl(testHash);

        assertEquals(testUrl, result);
    }
}

package faang.school.urlshortenerservice.service.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlNotExistException;
import faang.school.urlshortenerservice.hash.cache.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlValidator validator;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlMapper urlMapper;
    @Mock
    private HashCache hashCache;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void getUrl_shouldReturnLongUrlFromRedis_whenGetValidHash() throws JsonProcessingException {
        String hash = "3Rd56o";
        String url = "https://google.com";
        String jsonUrl = String.format("""
                {
                "hash": "%s",
                "url": "%s"
                }""", hash, url);
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(hash)).thenReturn(jsonUrl);

        String longUrl = urlService.getUrl(hash);

        verify(validator, times(1)).validateHash(hash);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperationsMock, times(1)).get(hash);
        verify(urlRepository, never()).findByHash(hash);
        verify(objectMapper, times(1)).readValue(anyString(), eq(Url.class));

        assertEquals(url, longUrl);
    }

    @Test
    void getUrl_shouldReturnLongUrlFromDB_whenGetValidHash() throws JsonProcessingException {
        String hash = "3Rd56o";
        String url = "https://google.com";
        Url urlModel = Url.builder().hash(hash).url(url).build();
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlModel));

        String longUrl = urlService.getUrl(hash);

        verify(validator, times(1)).validateHash(hash);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperationsMock, times(1)).get(hash);
        verify(urlRepository, times(1)).findByHash(hash);

        assertEquals(url, longUrl);
    }

    @Test
    void getUrl_shouldThrowException_whenGetInvalidHash() {
        String hash = "3Rd56o";
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotExistException.class, () -> urlService.getUrl(hash));

        verify(validator, times(1)).validateHash(hash);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperationsMock, times(1)).get(hash);
        verify(urlRepository, times(1)).findByHash(hash);
    }

    @Test
    void getUrl_shouldThrowException_whenHashIsNotExistInDb() throws JsonProcessingException {
        String invalidHash = ".1:re!";

        doThrow(DataValidationException.class).when(validator).validateHash(invalidHash);

        assertThrows(DataValidationException.class, () -> urlService.getUrl(invalidHash));

        verify(validator, times(1)).validateHash(invalidHash);
        verify(redisTemplate, never()).opsForValue();
        verify(urlRepository, never()).findByHash(invalidHash);
        verify(objectMapper, never()).readValue(anyString(), eq(Url.class));
    }

    @Test
    void createShortUrl_shouldReturnUrlDto_whenGetValidLongUrl() throws JsonProcessingException {
        String longUrl = "https://google.com";
        String hash = "DGc___";
        Url urlModel = Url.builder().hash(hash).url(longUrl).build();
        UrlDto urlDto = UrlDto.builder().hash(hash).url(longUrl).build();
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);

        doNothing().when(validator).validateUrl(longUrl);
        when(hashCache.getNextUniqueHash()).thenReturn(hash);
        when(urlRepository.save(urlModel)).thenReturn(urlModel);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(urlMapper.toDto(urlModel)).thenReturn(urlDto);

        urlService.createShortUrl(longUrl);

        InOrder inOrder = inOrder(validator, redisTemplate, urlRepository, urlMapper, hashCache, objectMapper, valueOperationsMock);
        inOrder.verify(validator, times(1)).validateUrl(longUrl);
        inOrder.verify(hashCache, times(1)).getNextUniqueHash();
        inOrder.verify(urlRepository, times(1)).save(urlModel);
        inOrder.verify(redisTemplate, times(1)).opsForValue();
        inOrder.verify(objectMapper, times(1)).writeValueAsString(urlModel);
        inOrder.verify(valueOperationsMock, times(1)).set(anyString(), anyString());
        inOrder.verify(urlMapper, times(1)).toDto(urlModel);
    }

    @Test
    void createShortUrl_shouldThrowException_whenGetInvalidLongUrl() throws JsonProcessingException {
        String invalidUrl = "not a url";

        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        doThrow(DataValidationException.class).when(validator).validateUrl(invalidUrl);

        assertThrows(DataValidationException.class, () -> urlService.createShortUrl(invalidUrl));

        verify(validator, times(1)).validateUrl(invalidUrl);
        verify(hashCache, never()).getNextUniqueHash();
        verify(urlRepository, never()).save(any(Url.class));
        verify(redisTemplate, never()).opsForValue();
        verify(objectMapper, never()).writeValueAsString(any(Url.class));
        verify(valueOperationsMock, never()).set(anyString(), anyString());
        verify(urlMapper, never()).toDto(any(Url.class));
    }

}
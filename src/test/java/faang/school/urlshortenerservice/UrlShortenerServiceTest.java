package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.excecption.InvalidUrlException;
import faang.school.urlshortenerservice.excecption.OriginalUrlNotFoundException;
import faang.school.urlshortenerservice.repository.ShortUrlRepository;
import faang.school.urlshortenerservice.service.CounterService;
import faang.school.urlshortenerservice.service.UrlShortenerRedisService;
import faang.school.urlshortenerservice.service.UrlShortenerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.atomic.AtomicLong;

import static faang.school.urlshortenerservice.messages.ErrorMessages.INVALID_URL;
import static faang.school.urlshortenerservice.messages.ErrorMessages.ORIGINAL_URL_NOT_FOUND;
import static faang.school.urlshortenerservice.messages.ErrorMessages.URL_CAN_T_BE_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UrlShortenerServiceTest {

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;

    @Mock
    private CounterService counterService;

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private UrlShortenerRedisService urlShortenerRedisService;

    private final String shortUrlPrefix = "http://CorporationX/";
    private final String hash = "hash";
    private final String shortUrl = shortUrlPrefix + hash;
    private final String originalUrl = "http://CorporationX/veryLongAddress";
    private final int counterBatchSize = 1000;
    private final AtomicLong counter = new AtomicLong(1);

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(urlShortenerService, "shortUrlPrefix", shortUrlPrefix);
        ReflectionTestUtils.setField(urlShortenerService, "counterBatchSize", counterBatchSize);
        ReflectionTestUtils.setField(urlShortenerService, "counter", counter);
    }

    @Test
    public void testCreateShortUrl_nullUrl() {
        InvalidUrlException exception = assertThrows(InvalidUrlException.class,
                () -> urlShortenerService.createShortUrl(null)
        );
        assertEquals(URL_CAN_T_BE_NULL, exception.getMessage());
    }

    @Test
    public void testCreateShortUrl_notUrl() {
        InvalidUrlException exception = assertThrows(InvalidUrlException.class,
                () -> urlShortenerService.createShortUrl("just text")
        );
        assertEquals(INVALID_URL + "just text", exception.getMessage());
    }

    @Test
    public void testCreateShortUrl_takeFromCache() {
        when(urlShortenerRedisService.getUrlHash(originalUrl)).thenReturn(hash);

        String result = urlShortenerService.createShortUrl(originalUrl);

        assertEquals(shortUrlPrefix + hash, result);
        verify(urlShortenerRedisService, timeout(1)).getUrlHash(originalUrl);
    }

    @Test
    public void testCreateShortUrl_takeFromDataBase_withoutIncrementingCounter() {
        when(urlShortenerRedisService.getUrlHash(originalUrl)).thenReturn(null);

        String result = urlShortenerService.createShortUrl(originalUrl);
        ArgumentCaptor<Url> argumentCaptor = ArgumentCaptor.forClass(Url.class);

        verify(shortUrlRepository, times(1)).save(argumentCaptor.capture());
        verify(urlShortenerRedisService, times(1)).getUrlHash(originalUrl);
        verify(urlShortenerRedisService, times(1))
                .addUrlHash(originalUrl, argumentCaptor.getValue().getHash());

        assertNotNull(argumentCaptor.getValue().getHash());
        assertEquals(originalUrl, argumentCaptor.getValue().getOriginalUrl());
        assertEquals(shortUrlPrefix + argumentCaptor.getValue().getHash(),
                argumentCaptor.getValue().getShortUrl());

        assertNotNull(result);
    }

    @Test
    public void testCreateShortUrl_takeFromDataBase_withIncrementingCounter() {
        when(urlShortenerRedisService.getUrlHash(originalUrl)).thenReturn(null);
        when(counterService.incrementAndGet()).thenReturn((long) counterBatchSize * 2);
        ReflectionTestUtils.setField(urlShortenerService, "counter", new AtomicLong(counterBatchSize));

        String result = urlShortenerService.createShortUrl(originalUrl);
        ArgumentCaptor<Url> argumentCaptor = ArgumentCaptor.forClass(Url.class);

        verify(counterService, times(1)).incrementAndGet();
        verify(shortUrlRepository, times(1)).save(argumentCaptor.capture());
        verify(urlShortenerRedisService, times(1)).getUrlHash(originalUrl);
        verify(urlShortenerRedisService, times(1))
                .addUrlHash(originalUrl, argumentCaptor.getValue().getHash());

        assertNotNull(argumentCaptor.getValue().getHash());
        assertEquals(originalUrl, argumentCaptor.getValue().getOriginalUrl());
        assertEquals(shortUrlPrefix + argumentCaptor.getValue().getHash(),
                argumentCaptor.getValue().getShortUrl());

        assertNotNull(result);
    }

    @Test
    public void testGetOriginalUrl_nullUrl() {
        InvalidUrlException exception = assertThrows(InvalidUrlException.class,
                () -> urlShortenerService.getOriginalUrl(null)
        );
        assertEquals(URL_CAN_T_BE_NULL, exception.getMessage());
    }

    @Test
    public void testGetOriginalUrl_notUrl() {
        InvalidUrlException exception = assertThrows(InvalidUrlException.class,
                () -> urlShortenerService.getOriginalUrl("just text")
        );
        assertEquals(INVALID_URL + "just text", exception.getMessage());
    }

    @Test
    public void testGetOriginalUrl_takeFromCache() {
        when(urlShortenerRedisService.getOriginalUrl(shortUrl)).thenReturn(originalUrl);

        String result = urlShortenerService.getOriginalUrl(shortUrl);

        assertEquals(originalUrl, result);
        verify(urlShortenerRedisService, times(1)).getOriginalUrl(shortUrl);
    }

    @Test
    public void testGetOriginalUrl_urlNotFound() {
        when(urlShortenerRedisService.getOriginalUrl(shortUrl)).thenReturn(null);
        when(shortUrlRepository.findOriginalUrlByShortUrl(shortUrl)).thenReturn(null);

        OriginalUrlNotFoundException exception = assertThrows(OriginalUrlNotFoundException.class,
                () -> urlShortenerService.getOriginalUrl(shortUrl)
        );

        assertEquals(ORIGINAL_URL_NOT_FOUND + shortUrl, exception.getMessage());
    }

    @Test
    public void testGetOriginalUrl_takeFromDataBase() {
        when(urlShortenerRedisService.getOriginalUrl(shortUrl)).thenReturn(null);
        when(shortUrlRepository.findOriginalUrlByShortUrl(shortUrl)).thenReturn(originalUrl);

        String result = urlShortenerService.getOriginalUrl(shortUrl);

        assertEquals(originalUrl, result);
        verify(shortUrlRepository, times(1)).findOriginalUrlByShortUrl(shortUrl);
        verify(urlShortenerRedisService, times(1)).addOriginalUrl(originalUrl, shortUrl);
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private HashService hashService;

    @Test
    void createShortUrlTest() {
        String hash = "1010101";
        String url = "http://qwe.ru";

        when(hashCache.getHash()).thenReturn(hash);

        String result = hashService.createShortUrl(url);

        verify(urlRepository, times(1)).save(any());
        assertEquals(result, hash);
    }

    @Test
    void getLongUrlTest() {
        String hash = "1010101";
        String url = "https://url.com";

        when(urlRepository.getUrl(hash)).thenReturn(Optional.of(url));

        String result = hashService.getLongUrl(hash);

        assertEquals(result, url);
    }

    @Test
    void getLongUrlTestNotValidHash() {
        String hash = "1010101";

        assertThrows(IllegalArgumentException.class, () -> hashService.getLongUrl(hash));
    }
}
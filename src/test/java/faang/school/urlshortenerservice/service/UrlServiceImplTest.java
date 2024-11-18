package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.url.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private RedisCacheRepository redisCacheRepository;
    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(urlService, "urlPath", "http://localhost:8080/api/v1/url/");
    }

    @Test
    void test_getShortUrl_OK() {
        String hash = "Q12w";
        UrlDto urlDto = UrlDto.builder().url("http://somelink.ru").build();
        when(hashCache.getHash()).thenReturn(hash);

        assertEquals("http://localhost:8080/api/v1/url/".concat(hash), urlService.getShortUrl(urlDto));
        verify(urlRepository).saveUrlWithNewHash(anyString(), anyString());
        verify(redisCacheRepository).save(anyString(), anyString());
    }
}

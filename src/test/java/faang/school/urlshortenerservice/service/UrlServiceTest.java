package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    private final static String TEST_URL = "https://faang-school.com/courses";
    private final static String HASH = "100000";

    private Url url;
    private String host;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    public void setUp() {
        host = "http://localhost:8080/api/v1";
        ReflectionTestUtils.setField(urlService, "host", "http://localhost:8080/api/v1");
        url = Url.builder()
                .url(TEST_URL)
                .hash(HASH)
                .build();
    }

    @Test
    public void testGetUrlFromDb() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(HASH);

        assertEquals(TEST_URL, result);

        verify(urlCacheRepository).findUrlByHash(HASH);
        verify(urlRepository).findById(HASH);
    }

    @Test
    public void testGetUrlFromCache() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.of(TEST_URL));

        String result = urlService.getUrl(HASH);

        assertEquals(TEST_URL, result);

        verify(urlCacheRepository).findUrlByHash(HASH);
        verifyNoInteractions(urlRepository);
    }

    @Test
    public void testSaveUrl() {
        when(hashCache.getHash()).thenReturn(HASH);
        UrlDto urlDto = new UrlDto(TEST_URL);

        UrlResponse result = urlService.save(urlDto);
        String shortUrl = "%s/%s".formatted(this.host, HASH);

        assertEquals(shortUrl, result.shortUrl());

        verify(urlCacheRepository).save(any(Url.class));
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    public void testGetNonExistingHash() {
        when(urlCacheRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.findById(HASH)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(HASH));
    }
}

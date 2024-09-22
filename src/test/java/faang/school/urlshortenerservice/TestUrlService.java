package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestUrlService {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @InjectMocks
    private UrlService urlService;


    @Test
    public void testFindUrlInRedis() {
        String url = "https://www.google.com";
        String key = "test";
        when(urlCacheRepository.getAssociation(any())).thenReturn(Optional.of(url));
        UrlDto dto = urlService.findUrl(key);
        assertThat(url).isEqualTo(dto.getUrl());
    }

    @Test
    public void testFindUrlInDB() {
        String url = "https://www.google.com";
        String key = "test";
        when(urlCacheRepository.getAssociation(any())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(any())).thenReturn(url);
        UrlDto dto = urlService.findUrl(key);
        assertThat(url).isEqualTo(dto.getUrl());
    }

    @Test
    public void testContainsUrlInDBSuccess() {
        ReflectionTestUtils.setField(urlService, "staticAddress", "https://sh.c/");
        String staticAddress = "https://sh.c/";
        String url = "https://www.google.com";
        String key = "test";
        UrlDto dto = new UrlDto();
        dto.setUrl(url);
        when(urlRepository.findByUrl(any())).thenReturn(Optional.of(key));
        UrlDto result = urlService.convertToShortUrl(dto);
        assertEquals(staticAddress.concat(key), result.getUrl());
    }

    @Test
    public void testContainsUrlInDBFailure() {
        ReflectionTestUtils.setField(urlService, "staticAddress", "https://sh.c/");
        String staticAddress = "https://sh.c/";
        String url = "https://www.google.com";
        String key = "test";
        UrlDto dto = new UrlDto();
        dto.setUrl(url);
        when(urlRepository.findByUrl(any())).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(key);
        UrlDto result = urlService.convertToShortUrl(dto);
        verify(urlCacheRepository, times(1)).saveAssociation(dto.getUrl(), key);
        verify(urlRepository, times(1)).saveAssociation(dto.getUrl(), key);
        assertEquals(staticAddress.concat(key), result.getUrl());
    }

}

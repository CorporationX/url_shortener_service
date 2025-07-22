package faang.school.urlshortenerservice.urlservicetest;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository repo;
    @Mock
    private UrlCacheRepository cache;

    @InjectMocks
    private UrlService service;

    @Test
    void getOriginalUrl_fromCache() {
        when(cache.find("xyz")).thenReturn(Optional.of("http://a"));
        String result = service.getOriginalUrl("xyz");
        assertEquals("http://a", result);
        verify(repo, never()).findById(any());
    }

    @Test
    void getOriginalUrl_fromDb() {
        when(cache.find("xyz")).thenReturn(Optional.empty());
        Url u = new Url();
        u.setHash("xyz");
        u.setUrl("http://b");
        when(repo.findById("xyz")).thenReturn(Optional.of(u));

        String result = service.getOriginalUrl("xyz");
        assertEquals("http://b", result);
        verify(cache).save("xyz", "http://b");
    }

    @Test
    void getOriginalUrl_notFound() {
        when(cache.find("xyz")).thenReturn(Optional.empty());
        when(repo.findById("xyz")).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrl("xyz"));
    }

    @Test
    void createShortUrl_happyPath() {
        when(hashCache.getHash()).thenReturn(Optional.of("h1"));
        RequestUrlDto dto = new RequestUrlDto();
        dto.setUrl("http://ok");
        String shortUrl = service.createShortUrl(dto.getUrl());
        assertTrue(shortUrl.endsWith("/h1"));
        verify(repo).save(any(Url.class));
        verify(cache).save("h1", "http://ok");
    }
}

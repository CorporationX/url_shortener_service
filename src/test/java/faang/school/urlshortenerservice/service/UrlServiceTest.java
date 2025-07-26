package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.url.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.storage.HashMemoryCache;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashMemoryCache hashMemoryCache;
    @InjectMocks
    private UrlService urlService;
    private Url url;

    @BeforeEach
    public void setUp() {
        url = new Url();
        url.setHash("tiopsFR");
        url.setUrl("https://com.faang.school/api/v1/users");
    }

    @Test
    public void testGetUrlByHash_successfully() {
        when(urlRepository.findById(url.getHash())).thenReturn(Optional.of(url));

        Url result = urlService.getUrlByHash(url.getHash());

        assertEquals(url, result);
        verify(urlRepository).findById(url.getHash());
    }

    @Test
    public void testGetUrlByHash_hashNotFound() {
        when(urlRepository.findById(url.getHash())).thenReturn(Optional.empty());

        assertThrows(HashNotFoundException.class, () -> urlService.getUrlByHash(url.getHash()));
        verify(urlRepository).findById(url.getHash());
    }

    @Test
    public void generateHash_successfully() {
        when(hashMemoryCache.getHash()).thenReturn(url.getHash());
        when(urlRepository.save(any())).thenReturn(url);

        Url result = urlService.generateHash(url.getUrl());

        assertEquals(result.getHash(), url.getHash());
        assertEquals(result.getUrl(), url.getUrl());
        verify(hashMemoryCache).getHash();
        verify(urlRepository).save(any());
    }
}

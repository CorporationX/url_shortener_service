package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    private final static String URL = "https://example.com";
    private final static String HASH = "jSbk3";

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    @Test
    void getHash_ShouldSaveToDbAndCache() {
        Url testEntity = Url.builder().hash(HASH).url(URL).build();

        when(hashCache.getHash()).thenReturn(new Hash(HASH));
        when(urlRepository.save(any(Url.class))).thenReturn(testEntity);

        String result = urlService.getHash(URL);

        assertEquals(HASH, result);

        verify(hashCache).getHash();
        verify(urlRepository).save(argThat(entity ->
                entity.getHash().equals(HASH) &&
                        entity.getUrl().equals(URL)
        ));
        verify(urlCacheRepository).save(URL, HASH);
    }

    @Test
    void getUrl_WhenExists_ShouldReturnUrl() {
        Url testEntity = Url.builder().hash(HASH).url(URL).build();

        when(urlRepository.findById(HASH)).thenReturn(Optional.of(testEntity));

        String result = urlService.getUrl(HASH);

        assertEquals(URL, result);
        verify(urlRepository).findById(HASH);
    }

    @Test
    void getUrl_WhenNotExists_ShouldThrowException() {
        String nonExistentHash = "invalid";

        when(urlRepository.findById(nonExistentHash)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                urlService.getUrl(nonExistentHash)
        );
    }
}
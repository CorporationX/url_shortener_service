package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.dto.CreateUrlRequest;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.cache.UrlCache;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Spy
    private UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);

    @Mock
    private UrlCache urlCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Captor
    private ArgumentCaptor<List<Hash>> hashesCaptor;

    private UrlServiceImpl service;

    @BeforeEach
    public void setUp() {
        UrlProperties urlProperties = new UrlProperties(URI.create("http://localhost:8080"));
        service = new UrlServiceImpl(
                urlProperties, hashCache, urlMapper,
                urlCache, hashRepository, urlRepository
        );
    }

    @Test
    @DisplayName("Should create short URL, persist entity and cache original URL")
    public void createCachesAndReturnsShortUrl() {
        CreateUrlRequest dto = new CreateUrlRequest("https://example.com/page");
        when(hashCache.getHash()).thenReturn("abc123");

        Url entity = new Url();
        entity.setHash("abc123");
        entity.setUrl(dto.url());
        doReturn(entity).when(urlMapper).toEntity(dto, "abc123");

        String shortUrl = service.create(dto);

        assertEquals("http://localhost:8080/abc123", shortUrl);
        verify(hashCache).getHash();
        verify(urlMapper).toEntity(dto, "abc123");
        verify(urlRepository).save(entity);
        verify(urlCache).put("abc123", "https://example.com/page");
        verifyNoMoreInteractions(urlRepository, urlCache, hashRepository);
    }

    @Test
    @DisplayName("Should return original URL from cache when cache hit")
    public void getOriginalUrlReturnsFromCacheOnHit() {
        when(urlCache.get("abc")).thenReturn("https://example.com");

        String result = service.getOriginalUrl("abc");

        assertEquals("https://example.com", result);
        verify(urlCache).get("abc");
        verify(urlRepository, never()).findById(anyString());
        verify(urlCache, never()).put(anyString(), anyString());
    }

    @Test
    @DisplayName("Should load from DB and put into cache when cache miss")
    public void getOriginalUrlLoadsAndCachesOnMiss() {
        when(urlCache.get("abc")).thenReturn(null);

        Url entity = new Url();
        entity.setHash("abc");
        entity.setUrl("https://example.com");
        when(urlRepository.findById("abc")).thenReturn(Optional.of(entity));

        String result = service.getOriginalUrl("abc");

        assertEquals("https://example.com", result);
        verify(urlCache).get("abc");
        verify(urlRepository).findById("abc");
        verify(urlCache).put("abc", "https://example.com");
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should throw UrlNotFoundException when hash is not found")
    public void getOriginalUrlThrowsWhenNotFound() {
        when(urlCache.get("missing")).thenReturn(null);
        when(urlRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrl("missing"));

        verify(urlCache).get("missing");
        verify(urlRepository).findById("missing");
        verify(urlCache, never()).put(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return empty list and not return hashes to pool when nothing deleted")
    public void cleanOldUrlsReturnsEmptyIfNoneDeleted() {
        LocalDateTime cutoff = LocalDateTime.now();
        when(urlRepository.deleteOldAndReturnHashes(cutoff)).thenReturn(List.of());

        List<String> result = service.cleanOldUrls(cutoff);

        assertTrue(result.isEmpty());
        verify(urlRepository).deleteOldAndReturnHashes(cutoff);
        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should return hashes and send them back to pool when deleted")
    public void cleanOldUrlsSavesHashesWhenDeleted() {
        LocalDateTime cutoff = LocalDateTime.now();
        List<String> hashes = List.of("h1", "h2", "h3");
        when(urlRepository.deleteOldAndReturnHashes(cutoff)).thenReturn(hashes);

        List<String> result = service.cleanOldUrls(cutoff);

        assertEquals(hashes, result);
        verify(urlRepository).deleteOldAndReturnHashes(cutoff);
        verify(hashRepository).saveAll(hashesCaptor.capture());

        List<Hash> saved = hashesCaptor.getValue();
        assertEquals(3, saved.size());
        assertEquals("h1", saved.get(0).getHash());
        assertEquals("h2", saved.get(1).getHash());
        assertEquals("h3", saved.get(2).getHash());
    }
}

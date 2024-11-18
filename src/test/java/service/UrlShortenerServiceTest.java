package service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {
    @InjectMocks
    private UrlShortenerService urlService;

    @Mock
    private HashRepository hashRepository;

    private final UrlCacheRepository urlCacheRepository = mock(UrlCacheRepository.class);
    private final UrlRepository urlRepository = mock(UrlRepository.class);
    @Mock
    private HashCache hashCache;

    private Url url;

    @BeforeEach
    void init() {
        url = Url.builder()
                .url("https://faang-school.com/courses/4jnzmndg/n7dv2bed")
                .build();
    }

    @Test
    @DisplayName("Create short link by url: cache repository is not null")
    void testCreateShortLinkCacheNotNull() {
        String cachedHash = "eaYt12";
        when(urlCacheRepository.getHashByUrl(anyString())).thenReturn(cachedHash);

        String cache = urlService.createShortLink(url);
        assertNotNull(cache);
        assertEquals(cachedHash, cache);
    }

    @Test
    @DisplayName("Create short link by url: url repository is not null")
    void testCreateShortLinkUrlRepositoryNotNull() {
        String cachedHash = "eaYt12";
        when(urlCacheRepository.getHashByUrl(anyString())).thenReturn(null);
        when(urlRepository.findHashByUrl(anyString())).thenReturn(Optional.of(cachedHash));

        String cache = urlService.createShortLink(url);
        assertNotNull(cache);
        assertEquals(cachedHash, cache);
    }

    @Test
    @DisplayName("Create short link by url: creating new link")
    void testCreateShortLinkCreateNewLink() {
        String hash = "W6gR4ly";
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any(Url.class))).thenReturn(new Url());

        urlService.createShortLink(url);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Get url by hash: url's cache found")
    void testGetUrlUrlCacheFound() {
        String url = "https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/59?assignee=712020%3A1af298d4-cbb2-4480-b0aa-765cba56660b";
        when(urlCacheRepository.getUrlByHash(anyString())).thenReturn(url);

        String cache = urlService.getUrl(url);
        assertNotNull(cache);
    }

    @Test
    @DisplayName("Get url by hash: url's cache is null")
    void testGetUrlUrlCacheIsNull() {
        String url = "https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/59?assignee=712020%3A1af298d4-cbb2-4480-b0aa-765cba56660b";
        when(urlCacheRepository.getUrlByHash(anyString())).thenReturn(null);

        when(urlRepository.findUrlByHash(anyString())).thenThrow();

        assertThrows(RuntimeException.class, () -> urlService.getUrl(url));
    }

    @Test
    @DisplayName("Get url by hash: url's found in DB")
    void testGetUrlUrlFoundInDB() {
        String hash = "W6gR4ly";
        when(urlCacheRepository.getUrlByHash(anyString())).thenReturn(null);

        when(urlRepository.findUrlByHash(anyString())).thenReturn(Optional.of(url));

        String savedUrl = urlService.getUrl(hash);
        assertNotNull(savedUrl);
        assertEquals(url.getUrl(), savedUrl);
    }

    @Test
    @DisplayName("Clean old urls: url's not present")
    void testCleanOldUrlsNotPresent() {
        String period = "1 year";
        when(urlRepository.getHashAndDeleteURL(anyString())).thenReturn(Optional.empty());

        urlService.cleanOldUrls(period);

        verify(urlRepository, times(1)).getHashAndDeleteURL(anyString());
        verify(hashRepository, times(0)).saveAll(anyList());
    }

    @Test
    @DisplayName("Clean old urls: url's are present")
    void testCleanOldUrlsArePresent() {
        String period = "1 year";
        List<Hash> hashes = Lists.newArrayList(new Hash("gfY6f5"), new Hash("jjdT6f"), new Hash("sdt6Df"));
        List<String> hashStrings = Lists.newArrayList("gfY6f5", "jjdT6f");

        when(urlRepository.getHashAndDeleteURL(anyString())).thenReturn(Optional.of(hashStrings));
        when(hashRepository.saveAll(anyList())).thenReturn(hashes);

        urlService.cleanOldUrls(period);

        verify(urlRepository, times(1)).getHashAndDeleteURL(anyString());
        verify(hashRepository, times(1)).saveAll(anyList());
    }
}

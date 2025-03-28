package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.generator.LocalHash;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashBatchRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService service;

    @Mock
    private UrlRepository urlRepository;

    @Spy
    private UrlMapperImpl urlMapper;

    @Mock
    private LocalHash localHash;

    @Mock
    private HashBatchRepository hashBatchRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private final int batchSize = 100;
    private final String originalHash = "abc123";

    private LocalDateTime oldDate;
    private Url oldUrl;
    private Hash hash;

    @BeforeEach
    public void setUp() {
        String originalHash = "abc123";

        oldUrl = Url.builder()
                .url("https://habr.com/ru/articles/733456/")
                .hash(originalHash)
                .createdAt(oldDate)
                .build();
        oldDate = LocalDateTime.now().minusYears(1);
        hash = new Hash(originalHash);

        ReflectionTestUtils.setField(service, "batchSize", batchSize);
    }

    @Test
    void shouldDeleteUrlAndSaveHash() {
        int pageNumber = 0;
        var oldUrls = new ArrayList<>(List.of(oldUrl));

        when(urlRepository.findByCreatedAtBefore(oldDate, PageRequest.of(pageNumber, batchSize)))
                .thenReturn(oldUrls);

        doNothing().when(urlRepository).deleteAll(oldUrls);
        doNothing().when(hashBatchRepository).saveHashByBatch(List.of(hash));

        service.deleteUrlAndSaveHash(oldDate, pageNumber);

        verify(urlRepository).findByCreatedAtBefore(oldDate, PageRequest.of(pageNumber, batchSize));
        verify(urlRepository).deleteAll(oldUrls);
        verify(hashBatchRepository).saveHashByBatch(anyList());
    }

    @Test
    void shouldDeleteOldUrl() {
        int totalCount = 1;
        int expectedPages = 1;
        List<Url> batch = List.of(oldUrl);

        when(urlRepository.countByCreatedAtBefore(any(LocalDateTime.class))).thenReturn(totalCount);
        when(urlRepository.findByCreatedAtBefore(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(batch);

        service.deleteOldUrl();

        verify(urlRepository).countByCreatedAtBefore(any(LocalDateTime.class));
        verify(urlRepository).deleteAll(batch);
        verify(hashBatchRepository, times(expectedPages)).saveHashByBatch(anyList());
    }

    @Test
    void shouldCreateShortUrl() {
        String serverName = "lehaps.com";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(serverName);

        String shortUrl = String.format("http://%s/%s", serverName, oldUrl.getHash());

        when(urlRepository.findUrlByUrl(oldUrl.getUrl())).thenReturn(Optional.empty());
        when(localHash.getHash()).thenReturn(hash);
        when(urlCacheRepository.saveUrlAndHash(oldUrl.getUrl(), hash)).thenReturn(originalHash);
        when(urlRepository.save(any(Url.class))).thenReturn(oldUrl);

        var shortUrlDto = new UrlDto(shortUrl);

        UrlDto result = service.createShortUrl(urlMapper.toDto(oldUrl), request);

        assertEquals(shortUrlDto, result);

        verify(urlRepository).findUrlByUrl(oldUrl.getUrl());
        verify(localHash).getHash();
        verify(urlCacheRepository).saveUrlAndHash(oldUrl.getUrl(), hash);
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void shouldThrowFindOriginalUrl() {
        when(urlCacheRepository.findUrl(originalHash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(originalHash)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> service.findOriginalUrl(originalHash));

        verify(urlRepository).findUrlByHash(originalHash);
        verify(urlCacheRepository).findUrl(originalHash);
    }

    @Test
    void shouldFindOriginalUrlByCache(){
        when(urlCacheRepository.findUrl(originalHash)).thenReturn(Optional.of(oldUrl.getUrl()));
        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(oldUrl.getUrl()))
                .build();

        assertEquals(service.findOriginalUrl(originalHash), response);

        verify(urlCacheRepository).findUrl(originalHash);
    }

    @Test
    void shouldFindOriginalUrlByRepository(){
        when(urlCacheRepository.findUrl(originalHash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(originalHash)).thenReturn(Optional.of(oldUrl));
        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(oldUrl.getUrl()))
                .build();

        assertEquals(service.findOriginalUrl(originalHash), response);

        verify(urlCacheRepository).findUrl(originalHash);
        verify(urlRepository).findUrlByHash(originalHash);
    }

}
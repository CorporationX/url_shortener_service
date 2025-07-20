package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashLocalCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dao.HashDao;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.HashNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UrlServiceImplTest {

    @Mock
    private HashLocalCache hashLocalCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCache urlCache;

    @Mock
    private HashDao hashDao;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Spy
    private RetryTemplate retryTemplate = new RetryTemplate();

    @Nested
    class ProcessLongUrlTests {

        @Test
        void whenUrlIsNew_thenGenerateHash_saveAndCache() {
            UrlDto dto = new UrlDto("https://new.com");
            String hash = "abc123";
            when(hashLocalCache.getHash()).thenReturn(hash);
            Url saved = Url.builder().url(dto.getUrl()).hash(hash).build();
            when(urlRepository.save(any(Url.class))).thenReturn(saved);

            String returnedHash = urlService.processLongUrl(dto);

            assertEquals(hash, returnedHash);
            verify(hashLocalCache, times(1)).getHash();
            verify(urlRepository, times(1)).save(any(Url.class));
            verify(urlCache, times(1)).addToCache(returnedHash, dto.getUrl());
        }

        @Test
        void whenSaveThrowsException_thenPropagateException() {
            UrlDto dto = new UrlDto("https://err.com");

            when(hashLocalCache.getHash()).thenReturn("abc123");
            when(urlRepository.save(any())).thenThrow(new RuntimeException("DB down"));

            assertThrows(RuntimeException.class, () -> urlService.processLongUrl(dto));
        }

        @Nested
        class DeleteOldReturningHashesTests {

            @Test
            void whenExpiredHashesPresent_thenInsertAndReturnList() {
                var list = List.of("a", "b", "c");
                when(urlRepository.deleteOldReturningHashes(any(), anyInt())).thenReturn(list);

                var result = urlService.deleteOldReturningHashes(LocalDateTime.now(), 3);

                assertEquals(result, list);
                verify(hashDao, times(1)).insertHashes(list);
            }
        }

        @Nested
        class GetOriginalUrlTests {

            @Test
            void whenHashExists_thenReturnUrl() {
                String originalUrl = "http://example.com";
                String hash = "abc123";
                when(urlRepository.findById(hash)).thenReturn(Optional.of(Url.builder().hash(hash)
                        .url(originalUrl).build()));

                String result = urlService.getOriginalUrl(hash);

                assertEquals(result, originalUrl);
            }

            @Test
            void whenHashNotFound_thenThrowHashNotFoundException() {
                String hash = "abc123";
                when(urlRepository.findById(hash)).thenReturn(Optional.empty());

                assertThrows(HashNotFoundException.class, () -> urlService.getOriginalUrl(hash));
            }
        }
    }
}

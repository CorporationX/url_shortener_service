package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CleanerServiceTest {

    @Mock
    private HashService hashService;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private CleanerService cleanerService;

    @Test
    void cleanUrlsAndSaveHashes_ShouldDeleteUrlsAndSaveHashes() {
        // given
        List<Url> urls = List.of(
            Url.builder()
                .id(1L)
                .hash("abc123")
                .originalUrl("https://example.com")
                .build(),
            Url.builder()
                .id(2L)
                .hash("def456")
                .originalUrl("https://example.org")
                .build()
        );

        List<String> hashes = List.of("abc123", "def456");

        // when
        cleanerService.cleanUrlsAndSaveHashes(urls);

        // then
        verify(hashService).saveHashes(hashes);
        verify(urlService).deleteUrls(urls);
    }
} 
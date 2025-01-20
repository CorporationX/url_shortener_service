package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanHashesServiceTest {
    @Mock
    private UrlService urlService;

    @Mock
    private HashService hashService;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private CleanHashesService cleanHashesService;

    @Test
    void testCleanHashes() {
        List<String> cleanedHashes = List.of("1", "a");
        when(urlService.cleanHashes()).thenReturn(cleanedHashes);
        doNothing().when(hashService).saveAll(eq(cleanedHashes));
        doNothing().when(urlCacheRepository).deleteAll(eq(cleanedHashes));
        cleanHashesService.cleanHashes();
    }
}
package faang.school.urlshortenerservice.service.urlcleanup;

import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of UrlCleanupServiceImplTest")
public class UrlCleanupServiceImplTest {

    private static final String URL_RETENTION_PERIOD = "1y";

    @Mock
    private HashService hashService;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlCleanupServiceImpl urlCleanupService;

    private Instant expiryDate;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(urlCleanupService, "urlRetentionPeriod", URL_RETENTION_PERIOD);
    }

    @Test
    @DisplayName("cleanExpiredUrls - without expired URLs")
    public void testCleanExpiredUrlsWithoutExpiredUrls() {
        when(urlRepository.cleanExpiredUrls(any(Instant.class))).thenReturn(List.of());

        urlCleanupService.cleanExpiredUrls();

        verify(hashService, never()).save(anyList());
    }

    @Test
    @DisplayName("cleanExpiredUrls - success")
    public void testCleanExpiredUrlsSuccess() {
        List<String> freeHashes = List.of("hash1", "hash2", "hash3");
        when(urlRepository.cleanExpiredUrls(any(Instant.class))).thenReturn(freeHashes);

        urlCleanupService.cleanExpiredUrls();

        verify(hashService, times(1)).save(freeHashes);
    }
}

package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpiredUrlsRemoverTest {

    @Mock
    private UrlService urlService;

    @Mock
    private HashService hashService;

    @InjectMocks
    private ExpiredUrlsRemover expiredUrlsRemover;

    private int expirationPeriodMonths;
    private List<Hash> hashes;

    @BeforeEach
    void setUp() {
        expirationPeriodMonths = 1;
        hashes = new ArrayList<>(
                List.of(Hash.builder().hash("1").build(),
                        Hash.builder().hash("2").build(),
                        Hash.builder().hash("3").build()));
        ReflectionTestUtils.setField(expiredUrlsRemover, "expirationPeriodMonths", expirationPeriodMonths);
    }

    @Test
    void testDeleteExpiredUrlsSuccess() {
        when(urlService.deleteExpiredLinks(any(LocalDateTime.class))).thenReturn(hashes);

        expiredUrlsRemover.deleteExpiredUrls();

        verify(urlService, times(1)).deleteExpiredLinks(any(LocalDateTime.class));
        verify(hashService, times(1)).saveHashes(hashes);
    }

    @Test
    void testDeleteExpiredUrls_NothingToDelete() {
        when(urlService.deleteExpiredLinks(any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        expiredUrlsRemover.deleteExpiredUrls();

        verify(urlService, times(1)).deleteExpiredLinks(any(LocalDateTime.class));
        verifyNoInteractions(hashService);
    }
}
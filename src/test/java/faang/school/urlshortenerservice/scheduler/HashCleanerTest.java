package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCleanerTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    private HashCleaner hashCleaner;

    @BeforeEach
    void setUp() {
        hashCleaner = new HashCleaner(hashRepository, urlRepository, Duration.ofDays(3));
    }

    @Test
    @DisplayName("Test remove of expired links and hash move to hash repo")
    void test_cleanExpiredHashes_success() {
        List<String> hashStrings = List.of("a", "b", "c");

        when(urlRepository.getExpiredHashes(any())).thenReturn(hashStrings);

        hashCleaner.cleanExpiredHashes();

        verify(urlRepository, times(1)).getExpiredHashes(any());
        verify(hashRepository, times(1)).saveAll(any());
    }
}
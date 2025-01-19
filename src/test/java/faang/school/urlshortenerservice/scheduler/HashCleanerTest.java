package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Captor
    ArgumentCaptor<List<Hash>> captor;

    @InjectMocks
    private HashCleaner hashCleaner;

    @Test
    @DisplayName("Test remove of expired links and hash move to hash repo")
    void test_cleanExpiredHashes_success() {
        ReflectionTestUtils.setField(hashCleaner, "intervalToClean", Duration.ofDays(2));
        List<String> hashStrings = List.of("a", "b", "c");

        when(urlRepository.getExpiredHashes(any())).thenReturn(hashStrings);

        hashCleaner.cleanExpiredHashes();

        verify(urlRepository, times(1)).getExpiredHashes(any());
        verify(hashRepository, times(1)).saveAll(captor.capture());

        assertEquals(3, captor.getValue().size());
        assertEquals(Hash.class, captor.getValue().get(0).getClass());
        assertEquals("a", captor.getValue().get(0).getHash());
    }

    @Test
    @DisplayName("Test remove of expired links and hash move to hash repo - no hashes to delete")
    void test_cleanExpiredHashes_NoHashesToDelete() {
        ReflectionTestUtils.setField(hashCleaner, "intervalToClean", Duration.ofDays(2));

        when(urlRepository.getExpiredHashes(any())).thenReturn(List.of());

        hashCleaner.cleanExpiredHashes();

        verify(urlRepository, times(1)).getExpiredHashes(any());
        verify(hashRepository, times(0)).saveAll(any());
    }
}
package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Captor
    private ArgumentCaptor<Iterable<Hash>> hashesIterableCaptor;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    void restoreUnusedHashes_shouldDeleteExpiredUrlsAndSaveHashes() {
        // Arrange
        ReflectionTestUtils.setField(cleanerScheduler, "entityTtlInSeconds", 86400);

        var testHashes = List.of("abc123", "def456");
        List<Url> expiredUrls = List.of(
                new Url(testHashes.get(0), "http://example.com/", LocalDateTime.now().minusDays(2)),
                new Url(testHashes.get(1), "http://example.org/", LocalDateTime.now().minusDays(3)));

        when(urlRepository.deleteAndGetExpiredEntities(any(LocalDateTime.class)))
                .thenReturn(expiredUrls);

        // Act
        cleanerScheduler.restoreUnusedHashes();

        // Assert
        verify(urlRepository).deleteAndGetExpiredEntities(any());

        verify(hashRepository).saveAll(hashesIterableCaptor.capture());
        var savedHashes = StreamSupport.stream(hashesIterableCaptor.getValue().spliterator(), false).toList();
        assertEquals(2, savedHashes.size());
        assertTrue(savedHashes.stream().map(Hash::getHash).anyMatch(testHashes.get(0)::equals));
        assertTrue(savedHashes.stream().map(Hash::getHash).anyMatch(testHashes.get(1)::equals));
    }

    @Test
    void restoreUnusedHashes_shouldHandleEmptyResults() {
        // Arrange
        when(urlRepository.deleteAndGetExpiredEntities(any())).thenReturn(List.of());

        // Act
        cleanerScheduler.restoreUnusedHashes();

        // Assert
        verify(urlRepository).deleteAndGetExpiredEntities(any());

        verify(hashRepository).saveAll(hashesIterableCaptor.capture());
        assertFalse(hashesIterableCaptor.getValue().iterator().hasNext());
    }

    @Test
    void restoreUnusedHashes_shouldHandleExceptions() {
        doThrow(new RuntimeException("Test exception")).when(urlRepository)
                .deleteAndGetExpiredEntities(any());

        assertDoesNotThrow(() -> cleanerScheduler.restoreUnusedHashes());
    }
}
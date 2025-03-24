package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.enums.HashStatus;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashLifecycleSchedulerTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Mock
    private FreeHashRepository freeHashRepository;

    @InjectMocks
    private HashLifecycleScheduler scheduler;

    @Test
    void updateHashStatuses_shouldUpdateStatusesCorrectly() {
        UrlMapping active = new UrlMapping();
        active.setHash("abc123");
        active.setStatus(HashStatus.ACTIVE);
        active.setExpiredAt(LocalDateTime.now().minusDays(1));

        UrlMapping waiting = new UrlMapping();
        waiting.setHash("def456");
        waiting.setStatus(HashStatus.WAITING);
        waiting.setExpiredAt(LocalDateTime.now().minusDays(10));

        when(urlMappingRepository.findByExpiredAtBefore(any())).thenReturn(List.of(active, waiting));

        scheduler.updateHashStatuses();

        verify(urlMappingRepository).saveAll(argThat((Iterable<UrlMapping> iterable) -> {
            int count = 0;
            boolean hasActiveUpdated = false;
            boolean hasWaitingUpdated = false;

            for (UrlMapping mapping : iterable) {
                if (mapping.getHash().equals("abc123") && mapping.getStatus() == HashStatus.WAITING) {
                    hasActiveUpdated = true;
                }
                if (mapping.getHash().equals("def456") && mapping.getStatus() == HashStatus.FREE) {
                    hasWaitingUpdated = true;
                }
                count++;
            }

            return count == 2 && hasActiveUpdated && hasWaitingUpdated;
        }));

        verifyNoInteractions(freeHashRepository);
    }

    @Test
    void cleanUpAndFreeHashes_shouldMoveFreeToFreeHashTable() {
        UrlMapping freeMapping = new UrlMapping();
        freeMapping.setHash("xyz789");
        freeMapping.setStatus(HashStatus.FREE);

        when(urlMappingRepository.findByStatus(HashStatus.FREE)).thenReturn(List.of(freeMapping));

        scheduler.cleanUpAndFreeHashes();

        verify(freeHashRepository).saveAll(argThat(iterable -> {
            if (iterable == null) return false;

            int count = 0;
            for (Object obj : iterable) {
                if (!(obj instanceof FreeHash freeHash)) return false;
                if (!"xyz789".equals(freeHash.getHash())) return false;
                count++;
            }
            return count == 1;
        }));

        verify(urlMappingRepository).deleteAll(List.of(freeMapping));
    }

    @Test
    void cleanUpAndFreeHashes_shouldDoNothingIfNoFreeMappings() {
        when(urlMappingRepository.findByStatus(HashStatus.FREE)).thenReturn(List.of());

        scheduler.cleanUpAndFreeHashes();

        verifyNoInteractions(freeHashRepository);
        verify(urlMappingRepository, never()).deleteAll(any());
    }
}
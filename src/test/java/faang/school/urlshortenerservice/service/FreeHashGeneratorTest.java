package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeHashGeneratorTest {

    @Mock
    private FreeHashRepository freeHashRepository;
    @InjectMocks
    private FreeHashGenerator freeHashGenerator;

    @Test
    void shouldSkipGenerationIfLockNotAcquired() {
        when(freeHashRepository.tryAdvisoryLock(99L)).thenReturn(false);

        freeHashGenerator.refillDb(10L);

        verify(freeHashRepository, never()).generateBatch(anyLong());
        verify(freeHashRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldGenerateAndSaveHashesIfLockAcquired() {
        when(freeHashRepository.tryAdvisoryLock(99L)).thenReturn(true);
        when(freeHashRepository.generateBatch(5L)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));

        freeHashGenerator.refillDb(5L);

        ArgumentCaptor<List<FreeHash>> captor = ArgumentCaptor.forClass(List.class);
        verify(freeHashRepository).saveAll(captor.capture());

        List<FreeHash> savedHashes = captor.getValue();
        assertThat(savedHashes).hasSize(5);
    }
}
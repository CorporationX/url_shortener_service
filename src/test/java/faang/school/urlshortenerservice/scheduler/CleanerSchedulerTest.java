package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleaner;

    @Captor
    private ArgumentCaptor<List<String>> hashCaptor;

    @Test
    void cleanOldUrls_shouldSaveHashesInTransaction() {
        when(urlRepository.deleteOldAndReturnHashes()).thenReturn(List.of("h1", "h2"));

        cleaner.cleanOldUrls();

        verify(hashRepository).saveAll(hashCaptor.capture());
        assertThat(hashCaptor.getValue()).containsExactlyInAnyOrder("h1", "h2");
    }

    @Test
    void cleanOldUrls_shouldRollbackOnFailure() {
        when(urlRepository.deleteOldAndReturnHashes()).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> cleaner.cleanOldUrls());

        verify(hashRepository, never()).saveAll(any());
    }
}

package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private HashRepository hashRepository;

    @Test
    @DisplayName("Delete expired urls and return hashes later than one year: success case")
    void deleteExpiredUrlsAndReturnHashesLaterThanOneYear_Success() {
        when(hashRepository.deleteHashesLaterThan(any())).thenReturn(List.of());

        cleanerScheduler.deleteExpiredUrlsAndReturnHashesLaterThanOneYear();

        verify(hashRepository, times(1)).deleteHashesLaterThan(any());
        verify(hashRepository, times(1)).saveAll(any());
    }
}

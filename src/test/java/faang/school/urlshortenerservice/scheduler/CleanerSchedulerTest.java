package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    UrlRepository urlRepository;

    @Mock
    HashRepository hashRepository;

    @InjectMocks
    CleanerScheduler cleanerScheduler;

    List<String> hashes;

    @BeforeEach
    void setUp() {
        hashes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should clean old URLs and save hashes successfully")
    void cleanOldUrls() {
        when(urlRepository.deleteOldUrlsAndReturnHashes(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.cleanOldUrls();

        verify(urlRepository).deleteOldUrlsAndReturnHashes(any(LocalDateTime.class));
        verify(hashRepository).save(hashes);
    }
}
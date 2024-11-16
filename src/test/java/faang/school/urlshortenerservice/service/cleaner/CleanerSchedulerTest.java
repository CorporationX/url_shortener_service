package faang.school.urlshortenerservice.service.cleaner;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    private static final String HASH = "hash";
    private List<String> existingHashes;

    @BeforeEach
    public void init() {
        existingHashes = Arrays.asList(HASH);
    }

    @Test
    @DisplayName("Success when clean DB")
    public void whenCleanDBThenDeleteOldUrlAndGetHashes() {
        when(urlRepository.getHashesAndDeleteOldUrls()).thenReturn(existingHashes);
        doNothing().when(hashRepository).saveAllHashesBatched(anyList());

        cleanerScheduler.cleanDB();

        verify(urlRepository).getHashesAndDeleteOldUrls();
        verify(hashRepository).saveAllHashesBatched(anyList());
    }
}
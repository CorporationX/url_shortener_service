package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.relational.core.sql.In;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CleanerSchedulerTest {
    @Mock
    HashRepository hashRepository;

    @InjectMocks
    CleanerScheduler cleanerScheduler;

    @Test
    void clearHashesTest() {
        List<String> oldHashes = new ArrayList<>(Arrays.asList("hash1", "hash2", "hash3"));
        Mockito.when(hashRepository.findAndDeleteOldHashes(Mockito.any())).thenReturn(oldHashes);
        cleanerScheduler.clearHashes();
        Mockito.verify(hashRepository, Mockito.times(1)).saveHashes(oldHashes);
    }
}

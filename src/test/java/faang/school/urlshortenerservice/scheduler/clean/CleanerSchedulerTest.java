package faang.school.urlshortenerservice.scheduler.clean;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource
class CleanerSchedulerTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Value("${spring.interval-hours}")
    private Long urlLifeTime;

    @Test
    public void testCleaner() {
        ReflectionTestUtils.setField(cleanerScheduler, "urlLifeTime", 8760L);
        LocalDateTime now = LocalDateTime.now().minusHours(8760);

        List<Hash> hashes = List.of(new Hash("exampleHash1", null), new Hash("exampleHash2", null));
        when(urlRepository.deleteOldUrlsAndReturnHashesAsHashEntities(any(LocalDateTime.class))).thenReturn(hashes);

        cleanerScheduler.cleaner();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashesAsHashEntities(captor.capture());

        LocalDateTime capturedDateTime = captor.getValue();

        long diff = Math.abs(capturedDateTime.getHour() - now.getHour());
        assert diff < 1 : "Дата и время не совпадают, разница в часах: " + diff;

        verify(hashRepository, times(1)).saveAll(hashes);
    }
}
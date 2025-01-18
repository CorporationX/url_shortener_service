package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @Test
    public void testDeleteOldRecords() {
        when(urlRepository.getOldUrls(any())).thenReturn(new ArrayList<>());

        cleanerScheduler.deleteOldRecords();

        verify(urlRepository).getOldUrls(any(LocalDateTime.class));
        verify(hashRepository).save(anyList(), anyInt(), eq(jdbcTemplate));
    }
}
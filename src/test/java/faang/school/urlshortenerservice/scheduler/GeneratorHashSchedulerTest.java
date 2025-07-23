package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GeneratorHashSchedulerTest {
    private static final Long HASH_RANGE = 10L;

    @Mock
    private UrlService urlService;
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private GeneratorHashScheduler scheduler;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Используем рефлексию для замены hashRange нашим моком
        Field hashRangeField = GeneratorHashScheduler.class.getDeclaredField("hashRange");
        hashRangeField.setAccessible(true);
        hashRangeField.set(scheduler, HASH_RANGE);
    }

    @Test
    public void testCleaner() {
        doNothing().when(urlService).clearOldUrls(anyInt());
        scheduler.doCleaner();
        verify(urlService).clearOldUrls(anyInt());
    }

    @Test
    public void testGenerateBatch() {
        doNothing().when(hashGenerator).generateBatchBySchedule(HASH_RANGE);
        scheduler.generateBatchBySchedule();
        verify(hashGenerator).generateBatchBySchedule(HASH_RANGE);
    }
}
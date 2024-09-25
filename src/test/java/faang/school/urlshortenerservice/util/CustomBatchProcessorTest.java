package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomBatchProcessorTest {

    public static final int CORE_BATCH_SIZE = 2;

    @Mock
    private ThreadPoolTaskExecutor hashBatchProcessingExecutor;

    private CustomBatchProcessor<String> batchProcessor;

    @Mock
    private Consumer<List<String>> task;

    @BeforeEach
    void setUp() {
        batchProcessor = new CustomBatchProcessor<>(hashBatchProcessingExecutor, CORE_BATCH_SIZE);
    }

    @Test
    void testProcessBatches() {
        List<String> source = Arrays.asList("item1", "item2", "item3", "item4", "item5");
        ArgumentCaptor<List<String>> batchCaptor = ArgumentCaptor.forClass(List.class);

        batchProcessor.processBatches(source, task);

        verify(task, times(3)).accept(batchCaptor.capture());
        List<List<String>> capturedBatches = batchCaptor.getAllValues();
        assertEquals(3, capturedBatches.size());
        assertEquals(Arrays.asList("item1", "item2"), capturedBatches.get(0));
        assertEquals(Arrays.asList("item3", "item4"), capturedBatches.get(1));
        assertEquals(Arrays.asList("item5"), capturedBatches.get(2));
    }
}
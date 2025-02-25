package faang.school.urlshortenerservice.modules.scheduler;

import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HistoryCleanerTest {
    @Mock
    private HashRepository hashRepository;
    @InjectMocks
    private HistoryCleaner historyCleaner;

    @Test
    void startJobSuccessTest() {
        historyCleaner.startJob();
        verify(hashRepository, times(1)).cleanDataOlder1Year();
    }

    @Test
    void startJobExceptionFailTest() {
        doThrow(new RuntimeException("Error. Exception.")).when(hashRepository).cleanDataOlder1Year();
        historyCleaner.startJob();
        verify(hashRepository, times(1)).cleanDataOlder1Year();
    }
}
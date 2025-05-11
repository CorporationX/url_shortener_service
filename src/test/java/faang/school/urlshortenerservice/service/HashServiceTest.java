package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private ExecutorService executorService;
    @InjectMocks
    private HashService hashService;

    @Test
    void givenCount_whenGetHashes_thenReturnHashesAndTriggerAsyncCheck() {
        int count = 3;
        List<String> expectedHashes = List.of("abc", "def", "ghi");
        when(hashRepository.getHashes(count)).thenReturn(expectedHashes);

        List<String> actualHashes = hashService.getHashes(count);

        verify(hashRepository).getHashes(count);
        verify(executorService).submit(any(Runnable.class));
        verifyNoMoreInteractions(hashRepository, executorService, hashGenerator);
        assertEquals(expectedHashes, actualHashes);
    }

    @Test
    void givenHashList_whenSaveFreeHashes_thenSaveToRepository() {
        List<String> hashList = List.of("xyz", "123");

        hashService.saveFreeHashes(hashList);

        verify(hashRepository).saveHashes(hashList);
        verifyNoMoreInteractions(hashRepository);
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.BatchHashRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {
    @InjectMocks
    private HashService hashService;

    @Mock
    private BatchHashRepository batchHashRepository;

    @Mock
    private HashRepository hashRepository;

    @Test
    void testSaveBatch() {
        doNothing().when(batchHashRepository).saveAll(anyList());
        hashService.saveBatch(anyList());

        verify(batchHashRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetUniqueNumbers() {
        when(hashRepository.getUniqueNumbers(1)).thenReturn(Collections.emptyList());

        hashService.getUniqueNumbers(1);

        verify(hashRepository, times(1)).getUniqueNumbers(1);
    }
}

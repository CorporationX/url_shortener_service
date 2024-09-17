package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {

    @Mock
    HashRepository hashRepository;
    @InjectMocks
    HashService hashService;

    @Test
    void testGetHashBatchSuccessful() {

        hashService.getHashBatch(anyInt());

        verify(hashRepository, times(1))
                .getHashBatch(anyInt());
    }

    @Test
    void testSaveAllHashesSuccessful() {
        Hash testHash = new Hash("abc");

        hashService.saveAllHashes(List.of("abc"));

        verify(hashRepository, times(1))
                .saveAll(List.of(testHash));
    }

    @Test
    void testGetUniqueNumbersSuccessful() {

        hashService.getUniqueNumbers(anyInt());

        verify(hashRepository, times(1))
                .getUniqueNumbers(anyInt());
    }
}
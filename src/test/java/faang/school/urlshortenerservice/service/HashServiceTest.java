package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private HashService hashService;

    @Test
    void getHashesSuccessTest() {
        List<Hash> hashes = List.of(new Hash("dfkj"), new Hash("dkjfk"), new Hash("kdjfdk"));
        when(hashRepository.getHashBatch(3)).thenReturn(hashes);
        assertDoesNotThrow(() -> {
            List<String> result = hashService.getHashes(3);
            assertEquals(3, result.size());
        });
        verify(hashRepository).getHashBatch(3);
        verify(hashGenerator, never()).generateBatch();
    }
}

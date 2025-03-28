package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @InjectMocks
    private HashService service;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Test
    void shouldGetHashes(){
        long count = 2L;
        Hash hash = new Hash("abc123");
        List<Hash> hashes = new ArrayList<>(List.of(hash));
        List<Hash> resultHashes = new ArrayList<>(List.of(hash, hash));

        when(hashRepository.getHashBatch(count)).thenReturn(hashes).thenReturn(resultHashes);
        doNothing().when(hashGenerator).generateBatch();

        assertEquals(resultHashes, service.getHashes(count));

        verify(hashRepository, times(2)).getHashBatch(count);
        verify(hashGenerator).generateBatch();
    }
}
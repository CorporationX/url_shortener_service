package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @Captor
    private ArgumentCaptor<List<Hash>> hashListCaptor;

    @Test
    void testSaveHashes() {
        // Given
        List<String> hashesStrings = List.of("hash1", "hash2", "hash3");

        // When
        hashService.saveHashes(hashesStrings);

        // Then
        verify(hashRepository).batchInsert(hashListCaptor.capture());
        List<Hash> hashes = hashListCaptor.getValue();
        assertEquals(hashesStrings.size(), hashes.size());
        assertEquals(hashesStrings.get(0), hashes.get(0).getHash());
    }
}
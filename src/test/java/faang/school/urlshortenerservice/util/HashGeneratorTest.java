package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 100);
    }

    @Test
    void testGetHashes_ShouldReturnRequestedAmount_WhenEnoughInRepository() {
        List<Hash> mockHashes = List.of(
                new Hash("hash1"),
                new Hash("hash2"),
                new Hash("hash3")
        );
        when(hashRepository.getHashBatch(3)).thenReturn(mockHashes);

        List<String> result = hashGenerator.getHashes(3);

        assertEquals(3, result.size());
        verify(hashRepository, times(1)).getHashBatch(3);
        verifyNoMoreInteractions(hashRepository);
        verifyNoInteractions(base62Encoder);
    }

    @Test
    void testGetHashes_ShouldGenerateNewBatch_WhenNotEnoughInRepository() {
        List<Hash> initialHashes = new ArrayList<>(List.of(new Hash("hash1")));
        List<Hash> newHashes = new ArrayList<>(List.of(new Hash("hash2"), new Hash("hash3")));

        when(hashRepository.getHashBatch(3)).thenReturn(initialHashes);
        when(hashRepository.getHashBatch(2)).thenReturn(newHashes);
        when(base62Encoder.encode(anyList())).thenReturn(List.of("hash2", "hash3"));

        List<String> result = hashGenerator.getHashes(3);

        assertEquals(3, result.size());
        verify(hashRepository, times(2)).getHashBatch(anyLong());
        verify(hashRepository).save(anyList());
        verify(base62Encoder).encode(anyList());
    }

    @Test
    void testGenerateBatch_ShouldSaveNewHashes() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("a", "b", "c");

        when(hashRepository.getUniqueNumbers(100)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(100);
        verify(base62Encoder).encode(numbers);
        verify(hashRepository).save(encodedHashes);
    }

    @Test
    void testGetHashes_ShouldHandleEmptyRepository() {
        List<Hash> emptyHashes = new ArrayList<>();
        List<Hash> newHashas = List.of(new Hash("new1"), new Hash("new2"));

        when(hashRepository.getHashBatch(anyLong()))
                .thenReturn(emptyHashes)
                .thenReturn(newHashas);
        when(base62Encoder.encode(anyList())).thenReturn(List.of("new1", "new2"));

        List<String> result = hashGenerator.getHashes(2);

        assertEquals(2, result.size());
        verify(hashRepository, times(2)).getHashBatch(anyLong());
        verify(hashRepository).save(anyList());
    }
}
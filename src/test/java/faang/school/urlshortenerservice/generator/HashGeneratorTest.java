package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Test
    void generateBatch_ShouldEncodeAndSaveHashes() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 1000);
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<Hash> encodedHashes = List.of(new Hash("1"), new Hash("2"), new Hash("3"));
        when(hashRepository.getUniqueNumbers(1000)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(encodedHashes);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(1000);
        verify(base62Encoder).encode(numbers);
        verify(hashRepository).saveAll(encodedHashes);
    }

    @Test
    void getHashes_ShouldReturnExistingHashes_WhenEnoughAvailable() {
        int amount = 2;
        List<Hash> existingHashes = List.of(new Hash("1"), new Hash("2"));
        when(hashRepository.getHashBatch(amount)).thenReturn(existingHashes);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(2, result.size());
        verify(hashRepository).getHashBatch(amount);
        verifyNoMoreInteractions(hashRepository);
        verifyNoInteractions(base62Encoder);
    }

    @Test
    void getHashes_ShouldGenerateNewBatch_WhenNotEnoughHashes() {
        int amount = 5;
        List<Hash> hashes = new ArrayList<>(List.of(new Hash("1"), new Hash("2")));
        List<Hash> newHashes = List.of(new Hash("3"), new Hash("4"), new Hash("5"));
        List<Long> numbers = List.of(10L, 20L, 30L);
        when(hashRepository.getHashBatch(amount)).thenReturn(hashes);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(newHashes);
        when(hashRepository.getHashBatch(amount - hashes.size())).thenReturn(newHashes);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(hashes.size(), result.size());
        verify(hashRepository).getHashBatch(amount);
        verify(hashRepository).getUniqueNumbers(anyInt());
        verify(base62Encoder).encode(numbers);
        verify(hashRepository).saveAll(newHashes);
        verify(hashRepository).getHashBatch(3);
    }
}
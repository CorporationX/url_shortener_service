package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    private Base62Converter base62Converter;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Captor
    private ArgumentCaptor<List<Hash>> hashCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 1000);
    }

    @Test
    void generateHash_shouldGenerateAndSaveHashes_whenNotEnoughExist() {
        long expectedSize = 5L;
        List<Hash> initialHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));
        List<Hash> newHashes = List.of(new Hash("hash4"), new Hash("hash5"));
        List<Long> numbers = List.of(1L, 2L);

        when(hashRepository.findAndDelete(expectedSize))
                .thenReturn(initialHashes).thenReturn(newHashes);
        when(hashRepository.getUniqueNumbers(1000)).thenReturn(numbers);
        when(base62Converter.convertToBase62(1L)).thenReturn("hash4");
        when(base62Converter.convertToBase62(2L)).thenReturn("hash5");

        List<String> result = hashGenerator.getHashBatch(expectedSize);

        assertEquals(expectedSize, result.size());
        verify(hashRepository, times(2)).findAndDelete(expectedSize);
        verify(hashRepository).getUniqueNumbers(1000);
        verify(hashRepository).saveAll(hashCaptor.capture());
    }

    @Test
    void generateHash_shouldGenerateAndSaveHashes_whenEnoughExist() {
        long expectedSize = 3L;
        List<Hash> initialHashes = List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"));

        when(hashRepository.findAndDelete(expectedSize)).thenReturn(initialHashes);

        List<String> result = hashGenerator.getHashBatch(expectedSize);

        assertEquals(expectedSize, result.size());
        verify(hashRepository).findAndDelete(expectedSize);
    }
}

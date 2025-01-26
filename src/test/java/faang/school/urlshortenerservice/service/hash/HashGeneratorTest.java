package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.util.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62 base62;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void generateHashes_ShouldGenerateAndSaveHashes() {
        int batchSize = 3;
        List<Long> uniqueValues = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getNextSequenceValues(batchSize)).thenReturn(uniqueValues);
        when(base62.encode(1L)).thenReturn("hash1");
        when(base62.encode(2L)).thenReturn("hash2");
        when(base62.encode(3L)).thenReturn("hash3");

        hashGenerator.generateHashes(batchSize);

        verify(hashRepository).getNextSequenceValues(batchSize);
        verify(base62, times(3)).encode(anyLong());

        ArgumentCaptor<List<Hash>> hashesCaptor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository).saveAll(hashesCaptor.capture());

        List<Hash> savedHashes = hashesCaptor.getValue();
        assertEquals(3, savedHashes.size());
        assertEquals("hash1", savedHashes.get(0).getHash());
        assertEquals("hash2", savedHashes.get(1).getHash());
        assertEquals("hash3", savedHashes.get(2).getHash());
    }

    @Test
    void generateHashes_WithEmptySequenceValues_ShouldNotSaveHashes() {
        int batchSize = 3;
        when(hashRepository.getNextSequenceValues(batchSize)).thenReturn(Collections.emptyList());

        hashGenerator.generateHashes(batchSize);

        verify(hashRepository).getNextSequenceValues(batchSize);
        verify(base62, never()).encode(anyLong());
        verify(hashRepository).saveAll(Collections.emptyList());
    }

    @Test
    void generateHashesAsync_ShouldCallGenerateHashes() {
        int batchSize = 3;
        List<Long> uniqueValues = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getNextSequenceValues(batchSize)).thenReturn(uniqueValues);
        when(base62.encode(anyLong())).thenReturn("hash");

        hashGenerator.generateHashesAsync(batchSize);

        verify(hashRepository).getNextSequenceValues(batchSize);
        verify(base62, times(3)).encode(anyLong());
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void generateHash_ShouldCreateHashWithEncodedValue() {
        long uniqueValue = 1L;
        when(base62.encode(uniqueValue)).thenReturn("encodedHash");

        Hash result = ReflectionTestUtils.invokeMethod(hashGenerator, "generateHash", uniqueValue);

        assertNotNull(result);
        assertEquals("encodedHash", result.getHash());
        verify(base62).encode(uniqueValue);
    }

    @Test
    void generateHashes_ShouldHandleNullSequenceValues() {
        int batchSize = 3;
        when(hashRepository.getNextSequenceValues(batchSize)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> hashGenerator.generateHashes(batchSize));

        verify(hashRepository).getNextSequenceValues(batchSize);
        verify(base62, never()).encode(anyLong());
        verify(hashRepository, never()).saveAll(anyList());
    }
}

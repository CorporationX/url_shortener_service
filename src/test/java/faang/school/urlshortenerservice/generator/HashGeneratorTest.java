package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    private final int range = 3;
    private final List<Long> uniqueNumbers = new ArrayList<>(List.of(1L,2L,3L));
    private final List<String> hashes = new ArrayList<>(List.of("b", "c", "d"));

    @Test
    public void testGenerateBatchWithGenerating(){
        ReflectionTestUtils.setField(hashGenerator, "range", range);
        when(hashRepository.getUniqueNumbers(range)).thenReturn(uniqueNumbers);
        when(base62Encoder.applyBase62Encoding(uniqueNumbers)).thenReturn(hashes);
        doNothing().when(hashRepository).saveAll(hashes);

        hashGenerator.generateBatch();

        InOrder inOrder = Mockito.inOrder(hashRepository, base62Encoder);
        inOrder.verify(hashRepository, times(1)).getUniqueNumbers(range);
        inOrder.verify(base62Encoder, times(1)).applyBase62Encoding(uniqueNumbers);
        inOrder.verify(hashRepository, times(1)).saveAll(hashes);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetHashesWithEnoughHashesInDataBase(){
        when(hashRepository.getHashBatch(range)).thenReturn(hashes);

        var result = hashGenerator.getHashes(range);

        InOrder inOrder = Mockito.inOrder(hashRepository);
        inOrder.verify(hashRepository, times(1)).getHashBatch(range);
        inOrder.verifyNoMoreInteractions();
        assertIterableEquals(hashes, result);
    }

    @Test
    public void testGetHashesWithGeneratingBatch(){
        hashes.remove(1);
        when(hashRepository.getHashBatch(range)).thenReturn(new ArrayList<>(hashes));
        ReflectionTestUtils.setField(hashGenerator, "range", range);
        when(hashRepository.getUniqueNumbers(range)).thenReturn(uniqueNumbers);
        when(base62Encoder.applyBase62Encoding(uniqueNumbers)).thenReturn(hashes);
        doNothing().when(hashRepository).saveAll(hashes);
        when(hashRepository.getHashBatch(range - hashes.size())).thenReturn(List.of("e"));

        var result = hashGenerator.getHashes(range);

        InOrder inOrder = Mockito.inOrder(hashRepository);
        inOrder.verify(hashRepository, times(1)).getHashBatch(range);
        inOrder.verify(hashRepository, times(1)).getUniqueNumbers(range);
        inOrder.verify(hashRepository, times(1)).saveAll(hashes);
        inOrder.verify(hashRepository, times(1)).getHashBatch(1);
        inOrder.verifyNoMoreInteractions();
        hashes.add("e");
        assertIterableEquals(hashes, result);
    }
}

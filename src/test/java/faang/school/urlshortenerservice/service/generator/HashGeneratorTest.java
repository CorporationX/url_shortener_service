package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    private final int maxRange = 3;
    @Captor
    private ArgumentCaptor<List<Hash>> hashCaptor;

    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(hashRepository, maxRange);
    }

    @Test
    void testGenerateBatch() {
        // given
        List<Long> uniqNumbers = List.of(1L, 2L, 3L);
        List<Hash> hashesToInsertExp = List.of(new Hash("b"), new Hash("c"), new Hash("d"));
        when(hashRepository.getUniqueNumbers(maxRange)).thenReturn(uniqNumbers);

        // when
        hashGenerator.generateBatch();

        // then
        verify(hashRepository, times(1)).saveAll(hashCaptor.capture());
        Assertions.assertEquals(hashesToInsertExp, hashCaptor.getValue());

    }

    @Test
    void testGetHashesEnough() {
        // given
        int amount = 3;
        List<Hash> hashes = List.of(new Hash("b"), new Hash("c"), new Hash("d"));
        when(hashRepository.findAndDelete(amount)).thenReturn(hashes);

        // when
        List<String> hashesActual = hashGenerator.getHashes(amount);

        // then
        verify(hashRepository, times(0)).getUniqueNumbers(Mockito.anyInt());
        verify(hashRepository, times(0)).findAndDelete(amount - hashes.size());
        Assertions.assertEquals(hashes.stream().map(Hash::getHash).toList(), hashesActual);
    }

    @Test
    void testGetHashesNotEnough() {
        // given
        int amount = 3;
        List<Hash> hashes = new ArrayList<>(List.of(new Hash("b"), new Hash("c")));
        when(hashRepository.findAndDelete(amount)).thenReturn(hashes);
        when(hashRepository.getUniqueNumbers(maxRange)).thenReturn(new ArrayList<>(List.of(3L, 4L, 5L)));
        when(hashRepository.findAndDelete(amount - (hashes.size()))).thenReturn(List.of(new Hash("d")));

        // when
        List<String> hashesActual = hashGenerator.getHashes(amount);

        // then
        verify(hashRepository, times(1)).getUniqueNumbers(Mockito.anyInt());
        verify(hashRepository, times(0)).findAndDelete(amount - hashes.size());
        Assertions.assertEquals(hashes.stream().map(Hash::getHash).toList(), hashesActual);
    }
}
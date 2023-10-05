package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @Mock
    HashRepository hashRepository;

    @InjectMocks
    HashGenerator hashGenerator;
    int maxRange = 5;
    List<Long> range = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    List<String> encodedRange = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", maxRange);
    }

    @Test
    void generateHashTest() {
        Mockito.when(hashRepository.generatedValues(maxRange)).thenReturn(range);
        hashGenerator.generateHash();
        Mockito.verify(hashRepository).saveHashes(encodedRange);
    }

    @Test
    void getHashTest() {
        Mockito.when(hashRepository.getHashBatch(Mockito.anyInt())).thenReturn(encodedRange);
        var res = hashGenerator.getHashes(maxRange);
        Assertions.assertEquals(res, encodedRange);
        Mockito.verify(hashRepository, Mockito.times(1)).getHashBatch(maxRange);
    }

    @Test
    void getHashNotEnoughTest() {
        List<String>  shortEncodedRange = new ArrayList<>(Arrays.asList("1", "2", "3"));
        Mockito.when(hashRepository.getHashBatch(Mockito.anyInt())).thenReturn(new ArrayList<>(shortEncodedRange));
        var res = hashGenerator.getHashes(maxRange);
        Assertions.assertNotEquals(res, shortEncodedRange);
        Mockito.verify(hashRepository, Mockito.times(2)).getHashBatch(maxRange);
    }


}

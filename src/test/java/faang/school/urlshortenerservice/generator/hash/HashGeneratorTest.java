package faang.school.urlshortenerservice.generator.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashFreeRepository;
import faang.school.urlshortenerservice.service.batchsaving.BatchSaveService;
import faang.school.urlshortenerservice.service.hash.HashFreeService;
import faang.school.urlshortenerservice.service.uniquenumber.UniqueNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private BatchSaveService batchSaveService;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private UniqueNumber uniqueNumber;

    @Mock
    private HashFreeService hashFreeService;

    @Mock
    private HashFreeRepository hashFreeRepository;

    private int quantity;
    private List<Long> numbers;
    private List<String> hash;


    @BeforeEach
    public void setUp() {
        quantity = 3;
        hashGenerator.setQuantity(3);
        numbers = new ArrayList<>(List.of(1L, 2L, 3L));
        hash = new ArrayList<>(List.of("n", "o", "p"));
    }

    @Test
    public void generateBatchTest() {
        when(hashFreeRepository.ifCountMinElements(anyInt())).thenReturn(false);
        when(uniqueNumber.getUniqueNumbers(quantity)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hash);
        when(hashFreeService.getHashBatch()).thenReturn(hash);

        List<String> result = hashGenerator.generateBatch();

        verify(batchSaveService, times(1)).saveEntities(hash, Hash.class);
        assertEquals(hash, result);
    }
}


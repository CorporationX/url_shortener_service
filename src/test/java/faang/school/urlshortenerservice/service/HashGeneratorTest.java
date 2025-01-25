package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder encoder;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void generateBatchSuccessTest() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<Hash> hashes = List.of(new Hash("b"), new Hash("c"), new Hash("d"));
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 1000);
        when(hashRepository.getUniqueNumbers(1000)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(hashes);
        assertDoesNotThrow(() -> {
            hashGenerator.generateBatch();
        });
        verify(hashRepository).getUniqueNumbers(1000);
        verify(encoder).encode(uniqueNumbers);
        verify(hashRepository).saveAll(hashes);
    }
}

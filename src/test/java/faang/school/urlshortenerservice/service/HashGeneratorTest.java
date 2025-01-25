package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Value("${hash.generate.size:3}")
    public Integer size;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(hashRepository, base62Encoder);
        ReflectionTestUtils.setField(hashGenerator, "size", 3);
    }

    @Test
    void generateHashesGenerateAndSaveHashesSuccessTest() {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("000001", "000002", "000003");

        when(hashRepository.getUniqueNumbers(3)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateHashes();

        verify(hashRepository).getUniqueNumbers(3);
        verify(base62Encoder).encode(uniqueNumbers);
        verify(hashRepository).saveAllHashes(encodedHashes);
    }

    @Test
    void generateHashesHandleEmptyUniqueNumbersListFailTest() {
        List<Long> uniqueNumbers = Arrays.asList();

        when(hashRepository.getUniqueNumbers(3)).thenReturn(uniqueNumbers);

        assertThrows(RuntimeException.class, () -> hashGenerator.generateHashes());

        verify(hashRepository).getUniqueNumbers(3);
        verify(base62Encoder, never()).encode(anyList());
        verify(hashRepository, never()).saveAllHashes(anyList());
    }

    @Test
    void generateHashesRunAsynchronouslySuccessTest() {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("000001", "000002");

        when(hashRepository.getUniqueNumbers(3)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        hashGenerator.generateHashes();

        verify(hashRepository, timeout(5000)).getUniqueNumbers(3); // Ensures async execution
        verify(base62Encoder, timeout(5000)).encode(uniqueNumbers);
        verify(hashRepository, timeout(5000)).saveAllHashes(encodedHashes);
    }
}

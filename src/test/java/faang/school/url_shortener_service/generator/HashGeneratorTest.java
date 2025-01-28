package faang.school.url_shortener_service.generator;


import faang.school.url_shortener_service.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @Spy
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;
    private final int hashBatchSize = 10000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "hashBatchSize", hashBatchSize);
    }

    @Test
    void testGetHashesAsyncSuccess() {
        List<Long> numbers = LongStream.range(1, 1000)
                .boxed()
                .toList();
        doReturn(base62Encoder.encode(numbers)).when(hashRepository).getHashBatch(hashBatchSize);
        hashGenerator.getHashesAsync(hashBatchSize)
                .thenAccept(System.out::println);

        verify(base62Encoder).encode(numbers);
        verify(hashRepository).getHashBatch(hashBatchSize);
        verify(hashRepository).getUniqueNumbers(hashBatchSize);
        verify(hashRepository).saveAll(anyList());
    }
}
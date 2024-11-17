package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final int maxRange = 10000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", maxRange);
    }

    @Test
    void testGetHashesAsyncSuccess() {
        List<Long> numbers = LongStream.range(1, 1000)
                .boxed()
                .toList();
        doReturn(base62Encoder.encode(numbers)).when(hashRepository).getHashBatch(maxRange);
        hashGenerator.getHashesAsync(maxRange)
                .thenAccept(System.out::println);

        verify(base62Encoder).encode(numbers);
        verify(hashRepository).getHashBatch(maxRange);
        verify(hashRepository).getUniqueNumbers(maxRange);
        verify(hashRepository).saveAll(anyList());
    }
}
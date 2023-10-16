package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder encoder;

    @Spy
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
        encoder = new Base62Encoder(executor);
        ReflectionTestUtils.setField(encoder, "alphabet", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(encoder, "batchSize", 4);
    }

    @Test
    void testEncode() {
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L);
        List<Hash> expected = List.of(
                new Hash("1"),
                new Hash("2"),
                new Hash("3"),
                new Hash("4"),
                new Hash("5"),
                new Hash("6"),
                new Hash("7"),
                new Hash("8"),
                new Hash("9"),
                new Hash("A"),
                new Hash("B"),
                new Hash("C"),
                new Hash("D"),
                new Hash("E"),
                new Hash("F"),
                new Hash("G"),
                new Hash("H"),
                new Hash("I"),
                new Hash("J"),
                new Hash("K")
        );

        List<Hash> actual = encoder.encode(numbers);

        assertEquals(expected, actual);
    }
}
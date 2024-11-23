package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    private final List<String> hashes = List.of("30TCwC", "30TCwD", "30TCwE");

    private final List<Long> range = List.of(
            50_000_000_000L,
            50_000_000_001L,
            50_000_000_002L
    );

    @Test
    public void testEncode() {
        List<String> result = base62Encoder.encode(range);

        assertEquals(result, hashes);
    }
}

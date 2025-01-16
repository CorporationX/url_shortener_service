package faang.school.urlshortenerservice.service;

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

    @Test
    public void encodeTest() {
        List<String> hashes = List.of("aoykuA", "boykuA", "coykuA");
        List<Long> range = List.of(10_000_000_000L, 10_000_000_001L, 10_000_000_002L);

        List<String> result = base62Encoder.encode(range);

        assertEquals(result, hashes);
    }
}
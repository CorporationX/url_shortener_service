package faang.school.urlshortenerservice.encoder;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {
    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncodeMultipleValues() {
        List<Long> input = Arrays.asList(0L, 1L, 62L, 12345L);
        List<String> expected = Arrays.asList(
                "",
                "1",
                "10",
                "3D7"
        );
        List<String> actual = encoder.encode(input);
        assertEquals(expected, actual);
    }
}

package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.base62encoder.Base62EncoderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        Base62EncoderConfig config = new Base62EncoderConfig();
        encoder = new Base62Encoder(config);
    }

    @Test
    public void testEncode() {
        List<Long> numbers = Arrays.asList(125L, 256L, 512L);
        List<String> encodedStrings = encoder.encode(numbers);
        assertNotNull(encodedStrings);
        assertEquals(3, encodedStrings.size());
    }

    @Test
    void testEncodeNumbers() {
        List<Long> numbers = Arrays.asList(1L, 22L, 2232323L);
        List<String> expected = Arrays.asList("1", "m", "dJm9");

        List<String> actual = encoder.encodeNumbers(numbers);

        assertEquals(expected, actual);
    }
}

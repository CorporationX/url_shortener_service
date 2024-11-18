package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    public void setUp() {
        encoder = new Base62Encoder();
    }

    @Test
    public void testBase62Encoder() {
        List<Long> numbers = new ArrayList<>();
        LongStream.range(20_000, 120_000).forEach(numbers::add);

        List<String> hashes = encoder.encode(numbers);

        assertEquals(100_000, hashes.size());
        String hash = hashes.iterator().next();
        assertTrue(hash.length() <= 6);
    }

}
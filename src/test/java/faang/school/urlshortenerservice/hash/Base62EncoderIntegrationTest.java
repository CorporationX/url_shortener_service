package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.AbstractIntegrationTest;
import faang.school.urlshortenerservice.exception.UniqueNumberOutOfBoundsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

class Base62EncoderIntegrationTest extends AbstractIntegrationTest {

    @Value("${hash.max-length:6}")
    private int maxHashLength;

    @Autowired
    private Base62Encoder base62Encoder;

    @Test
    void encode_shouldReturnTheExpectedValue() {
        Assertions.assertEquals("0", base62Encoder.encode(0));
        Assertions.assertEquals("1z", base62Encoder.encode(123));
    }

    @Test
    void encode_shouldThrowUniqueNumberOutOfBoundsException_whenThePassedValueIsGreaterThanTheMaximumValue() {
        Assertions.assertThrows(UniqueNumberOutOfBoundsException.class,
                () -> base62Encoder.encode(calculateMaxHashNumber() + 1));
    }

    private long calculateMaxHashNumber() {
        long temp = 1;
        for (int i = 0; i < maxHashLength; i++) {
            temp *= 62;
        }
        return temp - 1;
    }
}
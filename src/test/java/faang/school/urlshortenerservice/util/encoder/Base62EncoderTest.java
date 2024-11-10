package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "-123, -123",
            "1, 1",
            "62, 10",
            "123, 1z",
            "123456, W7E"
    })
    public void testEncodeSingleNumber(Long number, String correctResult) {
        Hash result = base62Encoder.encode(number);

        assertEquals(correctResult, result.getHash());
    }
}
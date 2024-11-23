package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @ParameterizedTest
    @CsvSource({
            "-1, -1",
            "0, 000000",
            "62, 000010",
            "56800235583, zzzzzz"
    })
    public void testEncodeSingleNumber(Long number, String correctResult) {
        Hash result = base62Encoder.encode(number);

        assertEquals(correctResult, result.getHash());
    }
}

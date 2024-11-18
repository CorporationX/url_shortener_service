package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @BeforeEach
    void setUp() {
        int hashLength = 6;
        ReflectionTestUtils.setField(base62Encoder, "hashLength", hashLength);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "-123, -123",
            "1, 000001",
            "62, 000010",
            "123, 00001z",
            "123456, 000W7E",
            "56800235585, 1000001"
    })
    public void testEncodeSingleNumber(Long number, String correctResult) {
        Hash result = base62Encoder.encode(number);

        assertEquals(correctResult, result.getHash());
    }
}
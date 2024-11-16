package faang.school.urlshortenerservice.utils.encoder;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    @DisplayName("Should correctly encode a positive number to Base62")
    void whenPositiveNumberThenEncodesCorrectly() {
        Long number = 12345L;
        HashDto result = base62Encoder.encode(number);

        assertEquals("0003d7", result.getHash());
    }

    @Test
    @DisplayName("Should correctly encode zero to Base62")
    void whenZeroThenEncodesCorrectly() {
        Long number = 0L;
        HashDto result = base62Encoder.encode(number);

        assertEquals("000000", result.getHash());
    }

    @Test
    @DisplayName("Should return string representation for negative numbers")
    void whenNegativeNumberThenReturnsStringRepresentation() {
        Long number = -12345L;
        HashDto result = base62Encoder.encode(number);

        assertEquals("-12345", result.getHash());
    }
}

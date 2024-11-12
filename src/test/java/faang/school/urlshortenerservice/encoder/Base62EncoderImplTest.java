package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class Base62EncoderImplTest {

    @InjectMocks
    private Base62EncoderImpl base62Encoder;

    @BeforeEach
    public void setUp() {
        base62Encoder = new Base62EncoderImpl();
        base62Encoder.setNumberOfCharacters(6);
    }

    @Test
    public void testEncodeSingle_withNumberWithinBaseRange() {
        long number = 25;
        Hash result = base62Encoder.encodeSingle(number);

        String expectedHash = "00000P";

        assertEquals(expectedHash, result.getHash());
    }

    @Test
    public void testEncodeSingle_withPadding() {
        long number = 17845;
        Hash result = base62Encoder.encodeSingle(number);

        assertEquals(6, result.getHash().length());
        System.out.println(result.getHash());
    }

    @Test
    public void testEncodeSingle_withSmallNumber() {
        long number = 1;
        Hash result = base62Encoder.encodeSingle(number);

        String expectedHash = "000001";
        assertEquals(expectedHash, result.getHash());
    }

    @Test
    public void testEncodeSingle_withLargeNumber() {
        long number = 1234567;
        Hash result = base62Encoder.encodeSingle(number);

        assertEquals(6, result.getHash().length());
        System.out.println(result.getHash());
    }

}
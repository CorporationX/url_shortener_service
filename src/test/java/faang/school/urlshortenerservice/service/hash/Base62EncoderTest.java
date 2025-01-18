package faang.school.urlshortenerservice.service.hash;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncodeNumber() {
        // arrange
        List<Integer> numbers = List.of(1, 2, 555, 123123, 88868, 1000985);
        List<String> expected = List.of("000001", "000002", "00008x", "000W1r", "000N7M", "004COv");

        // act
        List<String> result = encoder.encodeNumbers(numbers);
        System.out.println(result);

        // assert
        assertEquals(expected, result);
    }
}

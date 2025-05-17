package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void positiveEncode() {
        List<Long> inputNumbers = List.of(1L, 62L, 63L, 124L);
        List<String> expected = List.of("b", "ab", "bb", "ac");

        List<String> result = encoder.encode(inputNumbers);

        assertEquals(expected, result);
    }

    @Test
    public void negativeEncodeNumbersZero() {

        DataValidationException exception = assertThrows(
                DataValidationException.class, () -> encoder.encode(null)
        );

        assertEquals("Необходимо хотя бы одно число", exception.getMessage());
    }

}

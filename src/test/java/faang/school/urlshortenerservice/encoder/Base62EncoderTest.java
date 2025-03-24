package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder encoder;

    @Test
    public void encoder() {
        List<Long> numbers = List.of(123L);
        List<String> hash = List.of("9b");

        List<String> encoded = encoder.encode(numbers);

        assertEquals(hash.get(0), encoded.get(0));
    }

    @Test
    public void encoder_WithNullList() {
        List<Long> numbers = new ArrayList<>();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> encoder.encode(numbers));

        assertEquals(exception.getMessage(), "List must not be null or empty.");
    }

    @Test
    public void encoder_ListOfNegativeNumbers() {
        List<Long> numbers = List.of(-1L);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> encoder.encode(numbers));

        assertEquals(exception.getMessage(), "List must contain only Long values.");
    }
}